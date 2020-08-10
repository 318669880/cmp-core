package com.fit2cloud.mc.model;

import java.util.ArrayList;
import java.util.List;

public class ModelManagerExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    public ModelManagerExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
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
     * This method corresponds to the database table model_manager
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
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
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
     * This class corresponds to the database table model_manager
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

        public Criteria andModelAddressIsNull() {
            addCriterion("model_address is null");
            return (Criteria) this;
        }

        public Criteria andModelAddressIsNotNull() {
            addCriterion("model_address is not null");
            return (Criteria) this;
        }

        public Criteria andModelAddressEqualTo(String value) {
            addCriterion("model_address =", value, "modelAddress");
            return (Criteria) this;
        }

        public Criteria andModelAddressNotEqualTo(String value) {
            addCriterion("model_address <>", value, "modelAddress");
            return (Criteria) this;
        }

        public Criteria andModelAddressGreaterThan(String value) {
            addCriterion("model_address >", value, "modelAddress");
            return (Criteria) this;
        }

        public Criteria andModelAddressGreaterThanOrEqualTo(String value) {
            addCriterion("model_address >=", value, "modelAddress");
            return (Criteria) this;
        }

        public Criteria andModelAddressLessThan(String value) {
            addCriterion("model_address <", value, "modelAddress");
            return (Criteria) this;
        }

        public Criteria andModelAddressLessThanOrEqualTo(String value) {
            addCriterion("model_address <=", value, "modelAddress");
            return (Criteria) this;
        }

        public Criteria andModelAddressLike(String value) {
            addCriterion("model_address like", value, "modelAddress");
            return (Criteria) this;
        }

        public Criteria andModelAddressNotLike(String value) {
            addCriterion("model_address not like", value, "modelAddress");
            return (Criteria) this;
        }

        public Criteria andModelAddressIn(List<String> values) {
            addCriterion("model_address in", values, "modelAddress");
            return (Criteria) this;
        }

        public Criteria andModelAddressNotIn(List<String> values) {
            addCriterion("model_address not in", values, "modelAddress");
            return (Criteria) this;
        }

        public Criteria andModelAddressBetween(String value1, String value2) {
            addCriterion("model_address between", value1, value2, "modelAddress");
            return (Criteria) this;
        }

        public Criteria andModelAddressNotBetween(String value1, String value2) {
            addCriterion("model_address not between", value1, value2, "modelAddress");
            return (Criteria) this;
        }

        public Criteria andOnLineIsNull() {
            addCriterion("on_line is null");
            return (Criteria) this;
        }

        public Criteria andOnLineIsNotNull() {
            addCriterion("on_line is not null");
            return (Criteria) this;
        }

        public Criteria andOnLineEqualTo(Integer value) {
            addCriterion("on_line =", value, "onLine");
            return (Criteria) this;
        }

        public Criteria andOnLineNotEqualTo(Integer value) {
            addCriterion("on_line <>", value, "onLine");
            return (Criteria) this;
        }

        public Criteria andOnLineGreaterThan(Integer value) {
            addCriterion("on_line >", value, "onLine");
            return (Criteria) this;
        }

        public Criteria andOnLineGreaterThanOrEqualTo(Integer value) {
            addCriterion("on_line >=", value, "onLine");
            return (Criteria) this;
        }

        public Criteria andOnLineLessThan(Integer value) {
            addCriterion("on_line <", value, "onLine");
            return (Criteria) this;
        }

        public Criteria andOnLineLessThanOrEqualTo(Integer value) {
            addCriterion("on_line <=", value, "onLine");
            return (Criteria) this;
        }

        public Criteria andOnLineIn(List<Integer> values) {
            addCriterion("on_line in", values, "onLine");
            return (Criteria) this;
        }

        public Criteria andOnLineNotIn(List<Integer> values) {
            addCriterion("on_line not in", values, "onLine");
            return (Criteria) this;
        }

        public Criteria andOnLineBetween(Integer value1, Integer value2) {
            addCriterion("on_line between", value1, value2, "onLine");
            return (Criteria) this;
        }

        public Criteria andOnLineNotBetween(Integer value1, Integer value2) {
            addCriterion("on_line not between", value1, value2, "onLine");
            return (Criteria) this;
        }

        public Criteria andEnvIsNull() {
            addCriterion("env is null");
            return (Criteria) this;
        }

        public Criteria andEnvIsNotNull() {
            addCriterion("env is not null");
            return (Criteria) this;
        }

        public Criteria andEnvEqualTo(String value) {
            addCriterion("env =", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvNotEqualTo(String value) {
            addCriterion("env <>", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvGreaterThan(String value) {
            addCriterion("env >", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvGreaterThanOrEqualTo(String value) {
            addCriterion("env >=", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvLessThan(String value) {
            addCriterion("env <", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvLessThanOrEqualTo(String value) {
            addCriterion("env <=", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvLike(String value) {
            addCriterion("env like", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvNotLike(String value) {
            addCriterion("env not like", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvIn(List<String> values) {
            addCriterion("env in", values, "env");
            return (Criteria) this;
        }

        public Criteria andEnvNotIn(List<String> values) {
            addCriterion("env not in", values, "env");
            return (Criteria) this;
        }

        public Criteria andEnvBetween(String value1, String value2) {
            addCriterion("env between", value1, value2, "env");
            return (Criteria) this;
        }

        public Criteria andEnvNotBetween(String value1, String value2) {
            addCriterion("env not between", value1, value2, "env");
            return (Criteria) this;
        }

        public Criteria andValidateIsNull() {
            addCriterion("validate is null");
            return (Criteria) this;
        }

        public Criteria andValidateIsNotNull() {
            addCriterion("validate is not null");
            return (Criteria) this;
        }

        public Criteria andValidateEqualTo(Integer value) {
            addCriterion("validate =", value, "validate");
            return (Criteria) this;
        }

        public Criteria andValidateNotEqualTo(Integer value) {
            addCriterion("validate <>", value, "validate");
            return (Criteria) this;
        }

        public Criteria andValidateGreaterThan(Integer value) {
            addCriterion("validate >", value, "validate");
            return (Criteria) this;
        }

        public Criteria andValidateGreaterThanOrEqualTo(Integer value) {
            addCriterion("validate >=", value, "validate");
            return (Criteria) this;
        }

        public Criteria andValidateLessThan(Integer value) {
            addCriterion("validate <", value, "validate");
            return (Criteria) this;
        }

        public Criteria andValidateLessThanOrEqualTo(Integer value) {
            addCriterion("validate <=", value, "validate");
            return (Criteria) this;
        }

        public Criteria andValidateIn(List<Integer> values) {
            addCriterion("validate in", values, "validate");
            return (Criteria) this;
        }

        public Criteria andValidateNotIn(List<Integer> values) {
            addCriterion("validate not in", values, "validate");
            return (Criteria) this;
        }

        public Criteria andValidateBetween(Integer value1, Integer value2) {
            addCriterion("validate between", value1, value2, "validate");
            return (Criteria) this;
        }

        public Criteria andValidateNotBetween(Integer value1, Integer value2) {
            addCriterion("validate not between", value1, value2, "validate");
            return (Criteria) this;
        }

        public Criteria andSqlCriterion(String value) {
            addCriterion("(" + value + ")");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table model_manager
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
     * This class corresponds to the database table model_manager
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