package com.wangyu.sql.wrapper.wrapper;

import com.wangyu.sql.wrapper.enums.SqlKeyword;
import com.wangyu.sql.wrapper.model.GroupByModel;
import com.wangyu.sql.wrapper.model.LambdaSqlModel;
import com.wangyu.sql.wrapper.model.OrderByModel;
import com.wangyu.sql.wrapper.support.SerializedLambda;
import com.wangyu.sql.wrapper.support.WrapperConstant;
import com.wangyu.sql.wrapper.util.LambdaUtils;
import com.wangyu.sql.wrapper.support.SFunction;
import com.wangyu.sql.wrapper.util.StringUtils;
import com.wangyu.sql.wrapper.util.WrapperUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by wangyu
 * Date: 2019/9/5
 * Time: 10:41 AM
 * Description:
 */
public abstract class AbstractSqlWrapper<T,Children extends AbstractSqlWrapper<T,Children>>
        implements SqlCompare<Children,SFunction<T,?>>,SqlModel<SFunction<T,?>> {

    private int andSqlLevel = 0;

    private int orSqlLevel = 0;

    private boolean startBeforeSqlWord = false;

    private boolean removePre = false;

    private final String EMPTY = " ";

    private final String SPLIT = ",";

    List<LambdaSqlModel> lambdaSqlModelList = new ArrayList<>();

    protected Class<T> entityClass;

    protected final Children typedThis = (Children) this;

    protected StringBuffer selectSql = new StringBuffer();

    protected Map<String,Object> paramsMap = new HashMap<>();

    public AbstractSqlWrapper(Class<T> entityClass){
        this.entityClass = entityClass;
    }

    private Children addCondition(SFunction<T,?> column, SqlKeyword sqlKeyword, Object value, boolean ignoreNull){
        if(ignoreNull && StringUtils.ignoreNull(value)) return typedThis;
        String columnName = StringUtils.resolveFieldName(columnToString(column));
        String valueTemp = columnName+Math.abs(value.hashCode())+UUID.randomUUID().toString().replace("-","");
        if(StringUtils.isNotEmpty(value.toString())){
            paramsMap.put(valueTemp,value);
        }
        typedThis.lambdaSqlModelList.add(new LambdaSqlModel(columnName,":"+valueTemp,sqlKeyword));
        return typedThis;
    }

    private Children addConditionExt(SFunction<T,?> column, SqlKeyword sqlKeyword, Object value){
        String columnName = StringUtils.resolveFieldName(columnToString(column));
        typedThis.lambdaSqlModelList.add(new LambdaSqlModel(columnName,value,sqlKeyword));
        return typedThis;
    }

    private String columnToString(SFunction<T,?> column) {
        if(column == null) return null;
        return this.getColumn(LambdaUtils.resolve(column));
    }

    private String getColumn(SerializedLambda lambda) {
        return lambda.getImplMethodName();
    }

    private StringBuffer initHql(){
        selectSql.append("from").append(EMPTY).append(entityClass.getName()).append(EMPTY);
        return selectSql;
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    @Override
    public String getHql() {
        selectSql.setLength(0);
        initHql();
        if(lambdaSqlModelList.size()>0){
            selectSql.append("where ");
            for(int i = 0 ; i < lambdaSqlModelList.size() ; i++){
                if(i==0){
                    selectSql.append(columnToValues(true,lambdaSqlModelList.get(i)));
                }else{
                    selectSql.append(columnToValues(false,lambdaSqlModelList.get(i)));
                }
            }
        }
        return selectSql.toString();
    }

    protected  String columnToValues(boolean b, LambdaSqlModel lambdaSqlModel){
        StringBuffer whereSql = new StringBuffer(EMPTY);
        String wherePre = b?"":isAndOrLevel(lambdaSqlModel.getSqlKeyword())?"AND ":"";
        switch (lambdaSqlModel.getSqlKeyword()){
            case AND:
            case OR:whereSql.append(columnValueAndOr(lambdaSqlModel));removePre = true;break;
            case AND_LEFT:
            case AND_RIGHT:
            case OR_LEFT:
            case OR_RIGHT:whereSql.append(columnValueAndOr(lambdaSqlModel));break;
            case GROUP_BY:
            case ORDER_BY:String orderTemp = selectSql.toString().contains(lambdaSqlModel.getSqlKeyword().getKeyword()+EMPTY)?SPLIT:lambdaSqlModel.getSqlKeyword().getKeyword()+EMPTY;
                whereSql.append(orderTemp).append(lambdaSqlModel.getColumnName()).append(EMPTY).append(lambdaSqlModel.getValue());break;
            case IN:
            case NOT_IN:
                //whereSqlByValue(whereSql.append(wherePre),lambdaSqlModel.getColumnName(),lambdaSqlModel.getSqlKeyword(),columnValueListForInOrNotIn(lambdaSqlModel));break;
            default:whereSqlByValue(whereSql.append(wherePre),lambdaSqlModel.getColumnName(),lambdaSqlModel.getSqlKeyword(),
                    //StringUtils.quotaMark(lambdaSqlModel.getValue()));
                    String.valueOf(lambdaSqlModel.getValue()));
                    removePre = false;
        }
        return whereSql.toString();
    }

    private boolean isAndOrLevel(SqlKeyword sqlKeyword) {
        boolean startBeforeSqlWordTemp = false;
        switch (sqlKeyword){
            case AND_LEFT: andSqlLevel++;startBeforeSqlWord = true;break;
            case AND_RIGHT: andSqlLevel--;break;
            case OR_LEFT: orSqlLevel++;startBeforeSqlWord = true;break;
            case OR_RIGHT: orSqlLevel--;break;
            default:startBeforeSqlWordTemp = startBeforeSqlWord;startBeforeSqlWord = false;
        }
        if(removePre)return false;
        return !startBeforeSqlWordTemp||(WrapperUtil.isZeroLevel(andSqlLevel)&&WrapperUtil.isZeroLevel(orSqlLevel));
    }

    private String columnValueAndOr(LambdaSqlModel lambdaSqlModel) {
        removePre = false;
        return lambdaSqlModel.getSqlKeyword().getKeyword();
    }

    private void whereSqlByValue(StringBuffer whereSql,String columnName,SqlKeyword sqlKeyword,String value) {
        whereSql.append(columnName).append(EMPTY).append(sqlKeyword.getKeyword()).append(EMPTY).append(value).append(EMPTY);
    }

    /**
     * IN或者NOT IN
     * @param lambdaSqlModel
     * @return
     */
    private String columnValueListForInOrNotIn(LambdaSqlModel lambdaSqlModel) {
        removePre = false;
        List dataList = (List) lambdaSqlModel.getValue();
        String value = (String) dataList.stream().map(m->{return StringUtils.quotaMark(m).toString();}).collect(Collectors.joining(","));
        return "("+value+")";
    }

    @Override
    public Children groupBy(GroupByModel... groupByModels) {
        Arrays.asList(groupByModels).forEach(o ->{
            this.addConditionExt((SFunction<T, ?>) o.getColumn(),SqlKeyword.GROUP_BY,EMPTY);
        });
        return typedThis;
    }

    @Override
    public Children orderBy(OrderByModel... orderByModels) {
        Arrays.asList(orderByModels).forEach(o ->{
            this.addConditionExt((SFunction<T, ?>) o.getColumn(),SqlKeyword.ORDER_BY,o.getOrder());
        });
        return typedThis;
    }

    @Override
    public Children notIn(SFunction<T, ?> column, Object value, boolean b) {
        return this.addCondition(column,SqlKeyword.NOT_IN,value,b);
    }

    @Override
    public Children like(SFunction<T, ?> column, Object value, boolean b) {
        return this.addCondition(column,SqlKeyword.LIKE,value,b);
    }

    /**
     * 模糊查询方式
     * @param column
     * @param value
     * @param b
     * @param likeType 1前置模糊，2后置模糊，3前后置模糊
     * @return
     */
    public Children like(SFunction<T, ?> column, Object value, boolean b,int likeType) {
        if(b&&StringUtils.ignoreNull(value)){
            return typedThis;
        }else{
            value = likeType == WrapperConstant.LIKE_LEFT ? "%"+value:likeType == WrapperConstant.LIKE_RIGHT ? value+"%":"%"+value+"%";
        }
        return this.addCondition(column,SqlKeyword.LIKE,value,b);
    }

    @Override
    public Children eq(SFunction<T, ?> column, Object value, boolean b) {
        return this.addCondition(column,SqlKeyword.EQ,value,b);
    }

    @Override
    public Children ne(SFunction<T, ?> column, Object value, boolean b) {
        return this.addCondition(column,SqlKeyword.NE,value,b);
    }

    @Override
    public Children gt(SFunction<T, ?> column, Object value, boolean b) {
        return this.addCondition(column,SqlKeyword.GT,value,b);
    }

    @Override
    public Children ge(SFunction<T, ?> column, Object value, boolean b) {
        return this.addCondition(column,SqlKeyword.GE,value,b);
    }

    @Override
    public Children lt(SFunction<T, ?> column, Object value, boolean b) {
        return this.addCondition(column,SqlKeyword.LT,value,b);
    }

    @Override
    public Children le(SFunction<T, ?> column, Object value, boolean b) {
        return this.addCondition(column,SqlKeyword.LE,value,b);
    }

    @Override
    public Children isNull(SFunction<T, ?> column) {
        return this.addCondition(column,SqlKeyword.IS_NULL,null,false);
    }

    @Override
    public Children isNotNull(SFunction<T, ?> column) {
        return this.addCondition(column,SqlKeyword.IS_NOT_NULL,null,false);
    }

    @Override
    public Children in(SFunction<T, ?> column, Object value, boolean b) {
        return this.addCondition(column,SqlKeyword.IN,value,b);
    }

    @Override
    public Children and() {
        return this.addCondition(null,SqlKeyword.AND,null,false);
    }

    @Override
    public Children and(Function<Children, Children> function) {
        and();
        this.addCondition(null,SqlKeyword.AND_LEFT,null,false);
        function.apply(typedThis);
        this.addCondition(null,SqlKeyword.AND_RIGHT,null,false);
        return typedThis;
    }

    @Override
    public Children or() {
        return this.addCondition(null,SqlKeyword.OR,null,false);
    }

    @Override
    public Children or(Function<Children, Children> function) {
        or();
        this.addCondition(null,SqlKeyword.OR_LEFT,null,false);
        function.apply(typedThis);
        this.addCondition(null,SqlKeyword.OR_RIGHT,null,false);
        return typedThis;
    }
}
