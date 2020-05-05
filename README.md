# SqlWrapper
自动生成sql的Wrapper工具，可直接运用于JPA查询，方便快捷。
示例
```java
SqlWrapper<EntityModel> sqlWrapper = new SqlWrapper<>(EntityModel.class);
sqlWrapper.le(EntityModel::getId,12,true)
        .eq(EntityModel::getName,"zhangsan")
        .and(wrapper->wrapper.ge(EntityModel::getName,"169219").eq(EntityModel::getId,12))
        .or(wrapper->wrapper.le(EntityModel::getName,"name").or().eq(EntityModel::getId,88))
        .orderBy(sqlWrapper.newOrderByModel(EntityModel::getId),
                sqlWrapper.newOrderByModel(EntityModel::getName,SqlWrapperConfig.Order.DESC));
;
//查询列表数据
List<EntityModel> entityModels = jpaUtil.wrapper(sqlWrapper);
//查询单个实体
EntityModel model = jpaUtil.wrapperOne(sqlWrapper);
//分页查询
Page<EntityModel> page = jpaUtil.pageWrapper(sqlWrapper, PageRequest.of(1,10));
```
