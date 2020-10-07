package com.wangyu.sql.wrapper.wrapper;

import com.wangyu.sql.wrapper.constants.SqlWrapperConfig;
import com.wangyu.sql.wrapper.enums.SqlKeyword;
import com.wangyu.sql.wrapper.model.GroupByModel;
import com.wangyu.sql.wrapper.model.OutSqlModel;
import com.wangyu.sql.wrapper.model.inner.LambdaSqlModel;
import com.wangyu.sql.wrapper.model.OrderByModel;
import com.wangyu.sql.wrapper.support.SerializedLambda;
import com.wangyu.sql.wrapper.support.WrapperConstant;
import com.wangyu.sql.wrapper.util.LambdaUtils;
import com.wangyu.sql.wrapper.support.SFunction;
import com.wangyu.sql.wrapper.util.MessageFormatter;
import com.wangyu.sql.wrapper.util.StringUtils;
import com.wangyu.sql.wrapper.util.WrapperUtil;
import com.wangyu.sql.wrapper.wrapper.service.SqlCompare;
import com.wangyu.sql.wrapper.wrapper.service.SqlModel;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by wangyu
 * Date: 2019/9/5
 * Time: 10:41 AM
 * Description:
 */
public abstract class AbstractSqlWrapper<T, Children extends AbstractSqlWrapper<T, Children>>
        implements SqlCompare<Children, SFunction<T, ?>>, SqlModel<SFunction<T, ?>> {

    protected String hql;

    private int andSqlLevel = 0;

    private int orSqlLevel = 0;

    private boolean startBeforeSqlWord = false;

    /**
     * 完成一个完整的字段校验。是否不进行前缀校验
     */
    private boolean isNotCheckLink = false;

    private final String EMPTY0 = "";

    private final String EMPTY = " ";

    private final String SPLIT = ",";

    private final String WHERE = "WHERE";

    private final String LINK_DEFAULT = "AND";

    private OutSqlModel outSqlModel;

    protected List<LambdaSqlModel> lambdaSqlModelList = new ArrayList<>();

    protected Class<T> entityClass;

    protected final Children typedThis = (Children) this;

    protected StringBuffer selectSql = new StringBuffer();

    protected Map<String, Object> paramsMap = new HashMap<>();

    public AbstractSqlWrapper(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * 添加sql数据模型
     *
     * @param column     列名
     * @param sqlKeyword key类型
     * @param value      值
     * @param ignoreNull 为true并且value为空时，忽略条件判断
     * @return
     */
    private Children addCondition(SFunction<T, ?> column, SqlKeyword sqlKeyword, Object value, boolean ignoreNull) {
        // 参数可为空则不进行任何处理
        if (ignoreNull && StringUtils.ignoreNull(value)) return typedThis;

        // 连接关联字和内部关联字可以不指定对应列
        if (column == null
                && (sqlKeyword.getKeyFlag().equals(SqlWrapperConfig.Key.LINK)
                || sqlKeyword.getKeyFlag().equals(SqlWrapperConfig.Key.INTERIOR))) {
            typedThis.lambdaSqlModelList.add(new LambdaSqlModel(null, null, sqlKeyword));
            return typedThis;
        }

        // 解析获取列字段名
        String columnName = StringUtils.resolveFieldName(columnToString(column));

        // 判断关联字可以没有值
        if (ObjectUtils.isEmpty(value) && sqlKeyword.getKeyFlag().equals(SqlWrapperConfig.Key.JUDGE)) {
            typedThis.lambdaSqlModelList.add(new LambdaSqlModel(columnName, null, sqlKeyword));
            return typedThis;
        }

        // 定义唯一参数键
        String valueTemp = columnName + Math.abs(value.hashCode()) + UUID.randomUUID().toString().replace("-", "");
        paramsMap.put(valueTemp, value);
        typedThis.lambdaSqlModelList.add(new LambdaSqlModel(columnName, ":" + valueTemp, sqlKeyword));
        return typedThis;
    }

    /**
     * 用于分组或者排序sql模型处理
     *
     * @param column
     * @param sqlKeyword
     * @param value
     * @return
     */
    private Children addConditionExt(SFunction<T, ?> column, SqlKeyword sqlKeyword, Object value) {
        String columnName = StringUtils.resolveFieldName(columnToString(column));
        typedThis.lambdaSqlModelList.add(new LambdaSqlModel(columnName, value, sqlKeyword));
        return typedThis;
    }

    /**
     * 获取columnName转换成string
     *
     * @param column
     * @return
     */
    private String columnToString(SFunction<T, ?> column) {
        if (column == null) return null;
        return this.getColumn(LambdaUtils.resolve(column));
    }

    private String getColumn(SerializedLambda lambda) {
        return lambda.getImplMethodName();
    }

    /**
     * 初始化查询hql
     *
     * @return
     */
    private StringBuffer initHql() {
        if(outSqlModel != null){
            selectSql.append("select ")
                    .append(outSqlModel.getColumnNames())
                    .append(EMPTY)
            ;
        }
        selectSql.append("from").append(EMPTY)
                .append(entityClass.getName())
                .append(EMPTY).append("o").append(EMPTY);
        return selectSql;
    }

    /**
     * 获取参数
     *
     * @return
     */
    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    /**
     * 获取hql
     *
     * @return
     */
    @Override
    public String getHql() {
        selectSql.setLength(0);
        initHql();
        if (lambdaSqlModelList.size() > 0) {
            selectSql.append(WHERE);
            for (int i = 0; i < lambdaSqlModelList.size(); i++) {
                selectSql.append(columnToValues(selectSql.toString().endsWith(WHERE) ? true : false,
                        lambdaSqlModelList.get(i)));
            }
        }
        hql = selectSql.toString();
        return hql;
    }

    /**
     * 列数据转换
     *
     * @param notDefaultLink 是否默认添加默认连接符，true为不添加，false为添加
     * @param lambdaSqlModel sqlmodel转换
     * @return
     */
    protected String columnToValues(boolean notDefaultLink, LambdaSqlModel lambdaSqlModel) {
        StringBuffer whereSql = new StringBuffer(EMPTY);
        String preLinkDefault = getDefaultLink(notDefaultLink, lambdaSqlModel);
        switch (lambdaSqlModel.getSqlKeyword()) {
            case AND:
            case OR:
                whereSql.append(notDefaultLink? EMPTY0 : columnValueAndOr(lambdaSqlModel));
                isNotCheckLink = true;
                break;
            case AND_LEFT:
            case AND_RIGHT:
            case OR_LEFT:
            case OR_RIGHT:
                whereSql.append(columnValueAndOr(lambdaSqlModel));
                break;
            case GROUP_BY:
            case ORDER_BY:
                String orderTemp = selectSql.toString().contains(lambdaSqlModel.getSqlKeyword().getKeyword() + EMPTY) ? SPLIT : lambdaSqlModel.getSqlKeyword().getKeyword() + EMPTY;
                whereSql.append(orderTemp).append(lambdaSqlModel.getColumnName()).append(EMPTY).append(lambdaSqlModel.getValue());
                break;
            case IN:
            case NOT_IN:
            case EQ:
            case NE:
            case GT:
            case GE:
            case LT:
            case LE:
            case LIKE:
                whereSqlByValue(whereSql.append(preLinkDefault), lambdaSqlModel.getColumnName(), lambdaSqlModel.getSqlKeyword(),
                        String.valueOf(lambdaSqlModel.getValue()));
                isNotCheckLink = false;
                break;
            case IS_NULL:
            case IS_NOT_NULL:
                whereSqlByValue(whereSql.append(preLinkDefault), lambdaSqlModel.getColumnName(), lambdaSqlModel.getSqlKeyword(), EMPTY0);
                isNotCheckLink = false;
                break;
            case BETWEEN:
                whereSqlByValueBetween(whereSql.append(preLinkDefault), lambdaSqlModel.getColumnName(), lambdaSqlModel.getSqlKeyword(),
                        lambdaSqlModel.getValue(),lambdaSqlModel.getValue2());
                isNotCheckLink = false;
                break;
            default:
                break;

        }
        return whereSql.toString();
    }

    /**
     * 获取默认前缀关联字符
     *
     * @param notDefaultLink
     * @param lambdaSqlModel
     * @return
     */
    private String getDefaultLink(boolean notDefaultLink, LambdaSqlModel lambdaSqlModel) {
        return notDefaultLink ? EMPTY0 : isAndOrLevel(lambdaSqlModel.getSqlKeyword()) ? LINK_DEFAULT + EMPTY : EMPTY;
    }

    /**
     * 内部参数转换，默认AND添加剂
     *
     * @param sqlKeyword
     * @return
     */
    private boolean isAndOrLevel(SqlKeyword sqlKeyword) {
        boolean startBeforeSqlWordTemp = false;
        switch (sqlKeyword) {
            case AND_LEFT:
                andSqlLevel++;
                startBeforeSqlWord = true;
                break;
            case AND_RIGHT:
                andSqlLevel--;
                break;
            case OR_LEFT:
                orSqlLevel++;
                startBeforeSqlWord = true;
                break;
            case OR_RIGHT:
                orSqlLevel--;
                break;
            default:
                startBeforeSqlWordTemp = startBeforeSqlWord;
                startBeforeSqlWord = false;
        }
        if (isNotCheckLink) return false;
        return !startBeforeSqlWordTemp || (WrapperUtil.isZeroLevel(andSqlLevel) && WrapperUtil.isZeroLevel(orSqlLevel));
    }

    private String columnValueAndOr(LambdaSqlModel lambdaSqlModel) {
        isNotCheckLink = false;
        return lambdaSqlModel.getSqlKeyword().getKeyword();
    }

    private void whereSqlByValue(StringBuffer whereSql, String columnName, SqlKeyword sqlKeyword, String value) {
        whereSql.append(columnName).append(EMPTY).append(sqlKeyword.getKeyword()).append(EMPTY).append(value).append(EMPTY);
    }

    private void whereSqlByValueBetween(StringBuffer whereSql, String columnName, SqlKeyword sqlKeyword, Object valueStart,Object valueEnd) {
        whereSql.append(columnName).append(EMPTY)
                .append(MessageFormatter.format(sqlKeyword.getKeyword(),valueStart,valueEnd).getMessage()).append(EMPTY);
    }

    /**
     * IN或者NOT IN
     *
     * @param lambdaSqlModel
     * @return
     */
    private String columnValueListForInOrNotIn(LambdaSqlModel lambdaSqlModel) {
        isNotCheckLink = false;
        List dataList = (List) lambdaSqlModel.getValue();
        String value = (String) dataList.stream().map(m -> {
            return StringUtils.quotaMark(m).toString();
        }).collect(Collectors.joining(","));
        return "(" + value + ")";
    }

    /**
     * group by 分组
     * you can use by flows
     * sqlWrapper.newGroupByModel(Entity::getId)
     *
     * @param groupByModels
     * @return
     */
    @Override
    public Children groupBy(GroupByModel... groupByModels) {
        Arrays.asList(groupByModels).forEach(o -> {
            this.addConditionExt((SFunction<T, ?>) o.getColumn(), SqlKeyword.GROUP_BY, EMPTY);
        });
        return typedThis;
    }

    /**
     * order by
     * you can use by flows
     * sqlWrapper.newOrderByModel(Entity::getId)
     *
     * @param orderByModels
     * @return
     */
    @Override
    public Children orderBy(OrderByModel... orderByModels) {
        Arrays.asList(orderByModels).forEach(o -> {
            this.addConditionExt((SFunction<T, ?>) o.getColumn(), SqlKeyword.ORDER_BY, o.getOrder());
        });
        return typedThis;
    }

    /**
     * 输出实体对象
     * @param outSqlModel
     * @return
     */
    @Override
    public Children outModel(OutSqlModel outSqlModel) {
        this.outSqlModel = outSqlModel;
        return typedThis;
    }

    @Override
    public Children notIn(SFunction<T, ?> column, List value, boolean ignoreNull) {
        return this.addCondition(column, SqlKeyword.NOT_IN, value, ignoreNull);
    }

    @Override
    public Children like(SFunction<T, ?> column, Object value, boolean ignoreNull) {
        return this.addCondition(column, SqlKeyword.LIKE, value, ignoreNull);
    }

    /**
     * 模糊查询方式
     *
     * @param column
     * @param value
     * @param b
     * @param likeType 1前置模糊，2后置模糊，3前后置模糊
     * @return
     */
    public Children like(SFunction<T, ?> column, Object value, boolean b, int likeType) {
        if (b && StringUtils.ignoreNull(value)) {
            return typedThis;
        } else {
            value = likeType == WrapperConstant.LIKE_LEFT ? "%" + value : likeType == WrapperConstant.LIKE_RIGHT ? value + "%" : "%" + value + "%";
        }
        return this.addCondition(column, SqlKeyword.LIKE, value, b);
    }

    @Override
    public Children eq(SFunction<T, ?> column, Object value, boolean ignoreNull) {
        return this.addCondition(column, SqlKeyword.EQ, value, ignoreNull);
    }

    @Override
    public Children ne(SFunction<T, ?> column, Object value, boolean ignoreNull) {
        return this.addCondition(column, SqlKeyword.NE, value, ignoreNull);
    }

    @Override
    public Children gt(SFunction<T, ?> column, Object value, boolean ignoreNull) {
        return this.addCondition(column, SqlKeyword.GT, value, ignoreNull);
    }

    @Override
    public Children ge(SFunction<T, ?> column, Object value, boolean ignoreNull) {
        return this.addCondition(column, SqlKeyword.GE, value, ignoreNull);
    }

    @Override
    public Children lt(SFunction<T, ?> column, Object value, boolean ignoreNull) {
        return this.addCondition(column, SqlKeyword.LT, value, ignoreNull);
    }

    @Override
    public Children le(SFunction<T, ?> column, Object value, boolean ignoreNull) {
        return this.addCondition(column, SqlKeyword.LE, value, ignoreNull);
    }

    @Override
    public Children isNull(SFunction<T, ?> column) {
        return this.addCondition(column, SqlKeyword.IS_NULL, null, false);
    }

    @Override
    public Children isNotNull(SFunction<T, ?> column) {
        return this.addCondition(column, SqlKeyword.IS_NOT_NULL, null, false);
    }

    @Override
    public Children in(SFunction<T, ?> column, Collection value, boolean ignoreNull) {
        return this.addCondition(column, SqlKeyword.IN, value, ignoreNull);
    }

    @Override
    public Children and() {
        return this.addCondition(null, SqlKeyword.AND, null, false);
    }

    /**
     * lg:wrapper->wrapper.eq...
     *
     * @param function
     * @return
     */
    @Override
    public Children and(Function<Children, Children> function) {
        and();
        this.addCondition(null, SqlKeyword.AND_LEFT, null, false);
        function.apply(typedThis);
        this.addCondition(null, SqlKeyword.AND_RIGHT, null, false);
        return typedThis;
    }

    @Override
    public Children or() {
        return this.addCondition(null, SqlKeyword.OR, null, false);
    }

    /**
     * lg:wrapper->wrapper.eq...
     *
     * @param function
     * @return
     */
    @Override
    public Children or(Function<Children, Children> function) {
        or();
        this.addCondition(null, SqlKeyword.OR_LEFT, null, false);
        function.apply(typedThis);
        this.addCondition(null, SqlKeyword.OR_RIGHT, null, false);
        return typedThis;
    }

    @Override
    public Children between(SFunction<T, ?> column, Object startValue, Object endValue, boolean ignoreNull) {
        // 参数可为空则不进行任何处理
        if (ignoreNull && (StringUtils.ignoreNull(startValue) || StringUtils.ignoreNull(endValue))) return typedThis;
        // 解析获取列字段名
        String columnName = StringUtils.resolveFieldName(columnToString(column));
        // 定义唯一参数键
        String startValueTemp = columnName + Math.abs(startValue.hashCode()) + UUID.randomUUID().toString().replace("-", "");
        String startEndTemp = columnName + Math.abs(endValue.hashCode()) + UUID.randomUUID().toString().replace("-", "");

        paramsMap.put(startValueTemp, startValue);
        paramsMap.put(startEndTemp, endValue);
        typedThis.lambdaSqlModelList.add(new LambdaSqlModel(columnName, ":"+startValueTemp, ":"+startEndTemp,SqlKeyword.BETWEEN));

        return typedThis;
    }

    public OutSqlModel getOutSqlModel(){
        return outSqlModel;
    }

}
