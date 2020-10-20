package com.fit2cloud.commons.server.base.domain;

import java.util.ArrayList;
import java.util.List;

public class TagValueExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    public TagValueExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(String value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andTagKeyIsNull() {
            addCriterion("tag_key is null");
            return (Criteria) this;
        }

        public Criteria andTagKeyIsNotNull() {
            addCriterion("tag_key is not null");
            return (Criteria) this;
        }

        public Criteria andTagKeyEqualTo(String value) {
            addCriterion("tag_key =", value, "tagKey");
            return (Criteria) this;
        }

        public Criteria andTagKeyNotEqualTo(String value) {
            addCriterion("tag_key <>", value, "tagKey");
            return (Criteria) this;
        }

        public Criteria andTagKeyGreaterThan(String value) {
            addCriterion("tag_key >", value, "tagKey");
            return (Criteria) this;
        }

        public Criteria andTagKeyGreaterThanOrEqualTo(String value) {
            addCriterion("tag_key >=", value, "tagKey");
            return (Criteria) this;
        }

        public Criteria andTagKeyLessThan(String value) {
            addCriterion("tag_key <", value, "tagKey");
            return (Criteria) this;
        }

        public Criteria andTagKeyLessThanOrEqualTo(String value) {
            addCriterion("tag_key <=", value, "tagKey");
            return (Criteria) this;
        }

        public Criteria andTagKeyLike(String value) {
            addCriterion("tag_key like", value, "tagKey");
            return (Criteria) this;
        }

        public Criteria andTagKeyNotLike(String value) {
            addCriterion("tag_key not like", value, "tagKey");
            return (Criteria) this;
        }

        public Criteria andTagKeyIn(List<String> values) {
            addCriterion("tag_key in", values, "tagKey");
            return (Criteria) this;
        }

        public Criteria andTagKeyNotIn(List<String> values) {
            addCriterion("tag_key not in", values, "tagKey");
            return (Criteria) this;
        }

        public Criteria andTagKeyBetween(String value1, String value2) {
            addCriterion("tag_key between", value1, value2, "tagKey");
            return (Criteria) this;
        }

        public Criteria andTagKeyNotBetween(String value1, String value2) {
            addCriterion("tag_key not between", value1, value2, "tagKey");
            return (Criteria) this;
        }

        public Criteria andTagValueIsNull() {
            addCriterion("tag_value is null");
            return (Criteria) this;
        }

        public Criteria andTagValueIsNotNull() {
            addCriterion("tag_value is not null");
            return (Criteria) this;
        }

        public Criteria andTagValueEqualTo(String value) {
            addCriterion("tag_value =", value, "tagValue");
            return (Criteria) this;
        }

        public Criteria andTagValueNotEqualTo(String value) {
            addCriterion("tag_value <>", value, "tagValue");
            return (Criteria) this;
        }

        public Criteria andTagValueGreaterThan(String value) {
            addCriterion("tag_value >", value, "tagValue");
            return (Criteria) this;
        }

        public Criteria andTagValueGreaterThanOrEqualTo(String value) {
            addCriterion("tag_value >=", value, "tagValue");
            return (Criteria) this;
        }

        public Criteria andTagValueLessThan(String value) {
            addCriterion("tag_value <", value, "tagValue");
            return (Criteria) this;
        }

        public Criteria andTagValueLessThanOrEqualTo(String value) {
            addCriterion("tag_value <=", value, "tagValue");
            return (Criteria) this;
        }

        public Criteria andTagValueLike(String value) {
            addCriterion("tag_value like", value, "tagValue");
            return (Criteria) this;
        }

        public Criteria andTagValueNotLike(String value) {
            addCriterion("tag_value not like", value, "tagValue");
            return (Criteria) this;
        }

        public Criteria andTagValueIn(List<String> values) {
            addCriterion("tag_value in", values, "tagValue");
            return (Criteria) this;
        }

        public Criteria andTagValueNotIn(List<String> values) {
            addCriterion("tag_value not in", values, "tagValue");
            return (Criteria) this;
        }

        public Criteria andTagValueBetween(String value1, String value2) {
            addCriterion("tag_value between", value1, value2, "tagValue");
            return (Criteria) this;
        }

        public Criteria andTagValueNotBetween(String value1, String value2) {
            addCriterion("tag_value not between", value1, value2, "tagValue");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasIsNull() {
            addCriterion("tag_value_alias is null");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasIsNotNull() {
            addCriterion("tag_value_alias is not null");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasEqualTo(String value) {
            addCriterion("tag_value_alias =", value, "tagValueAlias");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasNotEqualTo(String value) {
            addCriterion("tag_value_alias <>", value, "tagValueAlias");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasGreaterThan(String value) {
            addCriterion("tag_value_alias >", value, "tagValueAlias");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasGreaterThanOrEqualTo(String value) {
            addCriterion("tag_value_alias >=", value, "tagValueAlias");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasLessThan(String value) {
            addCriterion("tag_value_alias <", value, "tagValueAlias");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasLessThanOrEqualTo(String value) {
            addCriterion("tag_value_alias <=", value, "tagValueAlias");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasLike(String value) {
            addCriterion("tag_value_alias like", value, "tagValueAlias");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasNotLike(String value) {
            addCriterion("tag_value_alias not like", value, "tagValueAlias");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasIn(List<String> values) {
            addCriterion("tag_value_alias in", values, "tagValueAlias");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasNotIn(List<String> values) {
            addCriterion("tag_value_alias not in", values, "tagValueAlias");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasBetween(String value1, String value2) {
            addCriterion("tag_value_alias between", value1, value2, "tagValueAlias");
            return (Criteria) this;
        }

        public Criteria andTagValueAliasNotBetween(String value1, String value2) {
            addCriterion("tag_value_alias not between", value1, value2, "tagValueAlias");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Long value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Long value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Long value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Long value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Long value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Long> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Long> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Long value1, Long value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Long value1, Long value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andTagIdIsNull() {
            addCriterion("tag_id is null");
            return (Criteria) this;
        }

        public Criteria andTagIdIsNotNull() {
            addCriterion("tag_id is not null");
            return (Criteria) this;
        }

        public Criteria andTagIdEqualTo(String value) {
            addCriterion("tag_id =", value, "tagId");
            return (Criteria) this;
        }

        public Criteria andTagIdNotEqualTo(String value) {
            addCriterion("tag_id <>", value, "tagId");
            return (Criteria) this;
        }

        public Criteria andTagIdGreaterThan(String value) {
            addCriterion("tag_id >", value, "tagId");
            return (Criteria) this;
        }

        public Criteria andTagIdGreaterThanOrEqualTo(String value) {
            addCriterion("tag_id >=", value, "tagId");
            return (Criteria) this;
        }

        public Criteria andTagIdLessThan(String value) {
            addCriterion("tag_id <", value, "tagId");
            return (Criteria) this;
        }

        public Criteria andTagIdLessThanOrEqualTo(String value) {
            addCriterion("tag_id <=", value, "tagId");
            return (Criteria) this;
        }

        public Criteria andTagIdLike(String value) {
            addCriterion("tag_id like", value, "tagId");
            return (Criteria) this;
        }

        public Criteria andTagIdNotLike(String value) {
            addCriterion("tag_id not like", value, "tagId");
            return (Criteria) this;
        }

        public Criteria andTagIdIn(List<String> values) {
            addCriterion("tag_id in", values, "tagId");
            return (Criteria) this;
        }

        public Criteria andTagIdNotIn(List<String> values) {
            addCriterion("tag_id not in", values, "tagId");
            return (Criteria) this;
        }

        public Criteria andTagIdBetween(String value1, String value2) {
            addCriterion("tag_id between", value1, value2, "tagId");
            return (Criteria) this;
        }

        public Criteria andTagIdNotBetween(String value1, String value2) {
            addCriterion("tag_id not between", value1, value2, "tagId");
            return (Criteria) this;
        }

        public Criteria andSqlCriterion(String value) {
            addCriterion("(" + value + ")");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table tag_value
     *
     * @mbg.generated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table tag_value
     *
     * @mbg.generated
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}