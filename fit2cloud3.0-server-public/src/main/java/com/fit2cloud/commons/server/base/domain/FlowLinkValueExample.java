package com.fit2cloud.commons.server.base.domain;

import java.util.ArrayList;
import java.util.List;

public class FlowLinkValueExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table flow_link_value
     *
     * @mbg.generated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table flow_link_value
     *
     * @mbg.generated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table flow_link_value
     *
     * @mbg.generated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flow_link_value
     *
     * @mbg.generated
     */
    public FlowLinkValueExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flow_link_value
     *
     * @mbg.generated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flow_link_value
     *
     * @mbg.generated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flow_link_value
     *
     * @mbg.generated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flow_link_value
     *
     * @mbg.generated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flow_link_value
     *
     * @mbg.generated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flow_link_value
     *
     * @mbg.generated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flow_link_value
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
     * This method corresponds to the database table flow_link_value
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
     * This method corresponds to the database table flow_link_value
     *
     * @mbg.generated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flow_link_value
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
     * This class corresponds to the database table flow_link_value
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

        public Criteria andLinkKeyIsNull() {
            addCriterion("link_key is null");
            return (Criteria) this;
        }

        public Criteria andLinkKeyIsNotNull() {
            addCriterion("link_key is not null");
            return (Criteria) this;
        }

        public Criteria andLinkKeyEqualTo(String value) {
            addCriterion("link_key =", value, "linkKey");
            return (Criteria) this;
        }

        public Criteria andLinkKeyNotEqualTo(String value) {
            addCriterion("link_key <>", value, "linkKey");
            return (Criteria) this;
        }

        public Criteria andLinkKeyGreaterThan(String value) {
            addCriterion("link_key >", value, "linkKey");
            return (Criteria) this;
        }

        public Criteria andLinkKeyGreaterThanOrEqualTo(String value) {
            addCriterion("link_key >=", value, "linkKey");
            return (Criteria) this;
        }

        public Criteria andLinkKeyLessThan(String value) {
            addCriterion("link_key <", value, "linkKey");
            return (Criteria) this;
        }

        public Criteria andLinkKeyLessThanOrEqualTo(String value) {
            addCriterion("link_key <=", value, "linkKey");
            return (Criteria) this;
        }

        public Criteria andLinkKeyLike(String value) {
            addCriterion("link_key like", value, "linkKey");
            return (Criteria) this;
        }

        public Criteria andLinkKeyNotLike(String value) {
            addCriterion("link_key not like", value, "linkKey");
            return (Criteria) this;
        }

        public Criteria andLinkKeyIn(List<String> values) {
            addCriterion("link_key in", values, "linkKey");
            return (Criteria) this;
        }

        public Criteria andLinkKeyNotIn(List<String> values) {
            addCriterion("link_key not in", values, "linkKey");
            return (Criteria) this;
        }

        public Criteria andLinkKeyBetween(String value1, String value2) {
            addCriterion("link_key between", value1, value2, "linkKey");
            return (Criteria) this;
        }

        public Criteria andLinkKeyNotBetween(String value1, String value2) {
            addCriterion("link_key not between", value1, value2, "linkKey");
            return (Criteria) this;
        }

        public Criteria andLinkValueIsNull() {
            addCriterion("link_value is null");
            return (Criteria) this;
        }

        public Criteria andLinkValueIsNotNull() {
            addCriterion("link_value is not null");
            return (Criteria) this;
        }

        public Criteria andLinkValueEqualTo(String value) {
            addCriterion("link_value =", value, "linkValue");
            return (Criteria) this;
        }

        public Criteria andLinkValueNotEqualTo(String value) {
            addCriterion("link_value <>", value, "linkValue");
            return (Criteria) this;
        }

        public Criteria andLinkValueGreaterThan(String value) {
            addCriterion("link_value >", value, "linkValue");
            return (Criteria) this;
        }

        public Criteria andLinkValueGreaterThanOrEqualTo(String value) {
            addCriterion("link_value >=", value, "linkValue");
            return (Criteria) this;
        }

        public Criteria andLinkValueLessThan(String value) {
            addCriterion("link_value <", value, "linkValue");
            return (Criteria) this;
        }

        public Criteria andLinkValueLessThanOrEqualTo(String value) {
            addCriterion("link_value <=", value, "linkValue");
            return (Criteria) this;
        }

        public Criteria andLinkValueLike(String value) {
            addCriterion("link_value like", value, "linkValue");
            return (Criteria) this;
        }

        public Criteria andLinkValueNotLike(String value) {
            addCriterion("link_value not like", value, "linkValue");
            return (Criteria) this;
        }

        public Criteria andLinkValueIn(List<String> values) {
            addCriterion("link_value in", values, "linkValue");
            return (Criteria) this;
        }

        public Criteria andLinkValueNotIn(List<String> values) {
            addCriterion("link_value not in", values, "linkValue");
            return (Criteria) this;
        }

        public Criteria andLinkValueBetween(String value1, String value2) {
            addCriterion("link_value between", value1, value2, "linkValue");
            return (Criteria) this;
        }

        public Criteria andLinkValueNotBetween(String value1, String value2) {
            addCriterion("link_value not between", value1, value2, "linkValue");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasIsNull() {
            addCriterion("link_value_alias is null");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasIsNotNull() {
            addCriterion("link_value_alias is not null");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasEqualTo(String value) {
            addCriterion("link_value_alias =", value, "linkValueAlias");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasNotEqualTo(String value) {
            addCriterion("link_value_alias <>", value, "linkValueAlias");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasGreaterThan(String value) {
            addCriterion("link_value_alias >", value, "linkValueAlias");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasGreaterThanOrEqualTo(String value) {
            addCriterion("link_value_alias >=", value, "linkValueAlias");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasLessThan(String value) {
            addCriterion("link_value_alias <", value, "linkValueAlias");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasLessThanOrEqualTo(String value) {
            addCriterion("link_value_alias <=", value, "linkValueAlias");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasLike(String value) {
            addCriterion("link_value_alias like", value, "linkValueAlias");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasNotLike(String value) {
            addCriterion("link_value_alias not like", value, "linkValueAlias");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasIn(List<String> values) {
            addCriterion("link_value_alias in", values, "linkValueAlias");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasNotIn(List<String> values) {
            addCriterion("link_value_alias not in", values, "linkValueAlias");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasBetween(String value1, String value2) {
            addCriterion("link_value_alias between", value1, value2, "linkValueAlias");
            return (Criteria) this;
        }

        public Criteria andLinkValueAliasNotBetween(String value1, String value2) {
            addCriterion("link_value_alias not between", value1, value2, "linkValueAlias");
            return (Criteria) this;
        }

        public Criteria andLinkValuePriorityIsNull() {
            addCriterion("link_value_priority is null");
            return (Criteria) this;
        }

        public Criteria andLinkValuePriorityIsNotNull() {
            addCriterion("link_value_priority is not null");
            return (Criteria) this;
        }

        public Criteria andLinkValuePriorityEqualTo(Long value) {
            addCriterion("link_value_priority =", value, "linkValuePriority");
            return (Criteria) this;
        }

        public Criteria andLinkValuePriorityNotEqualTo(Long value) {
            addCriterion("link_value_priority <>", value, "linkValuePriority");
            return (Criteria) this;
        }

        public Criteria andLinkValuePriorityGreaterThan(Long value) {
            addCriterion("link_value_priority >", value, "linkValuePriority");
            return (Criteria) this;
        }

        public Criteria andLinkValuePriorityGreaterThanOrEqualTo(Long value) {
            addCriterion("link_value_priority >=", value, "linkValuePriority");
            return (Criteria) this;
        }

        public Criteria andLinkValuePriorityLessThan(Long value) {
            addCriterion("link_value_priority <", value, "linkValuePriority");
            return (Criteria) this;
        }

        public Criteria andLinkValuePriorityLessThanOrEqualTo(Long value) {
            addCriterion("link_value_priority <=", value, "linkValuePriority");
            return (Criteria) this;
        }

        public Criteria andLinkValuePriorityIn(List<Long> values) {
            addCriterion("link_value_priority in", values, "linkValuePriority");
            return (Criteria) this;
        }

        public Criteria andLinkValuePriorityNotIn(List<Long> values) {
            addCriterion("link_value_priority not in", values, "linkValuePriority");
            return (Criteria) this;
        }

        public Criteria andLinkValuePriorityBetween(Long value1, Long value2) {
            addCriterion("link_value_priority between", value1, value2, "linkValuePriority");
            return (Criteria) this;
        }

        public Criteria andLinkValuePriorityNotBetween(Long value1, Long value2) {
            addCriterion("link_value_priority not between", value1, value2, "linkValuePriority");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeIsNull() {
            addCriterion("assignee_type is null");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeIsNotNull() {
            addCriterion("assignee_type is not null");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeEqualTo(String value) {
            addCriterion("assignee_type =", value, "assigneeType");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeNotEqualTo(String value) {
            addCriterion("assignee_type <>", value, "assigneeType");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeGreaterThan(String value) {
            addCriterion("assignee_type >", value, "assigneeType");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeGreaterThanOrEqualTo(String value) {
            addCriterion("assignee_type >=", value, "assigneeType");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeLessThan(String value) {
            addCriterion("assignee_type <", value, "assigneeType");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeLessThanOrEqualTo(String value) {
            addCriterion("assignee_type <=", value, "assigneeType");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeLike(String value) {
            addCriterion("assignee_type like", value, "assigneeType");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeNotLike(String value) {
            addCriterion("assignee_type not like", value, "assigneeType");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeIn(List<String> values) {
            addCriterion("assignee_type in", values, "assigneeType");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeNotIn(List<String> values) {
            addCriterion("assignee_type not in", values, "assigneeType");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeBetween(String value1, String value2) {
            addCriterion("assignee_type between", value1, value2, "assigneeType");
            return (Criteria) this;
        }

        public Criteria andAssigneeTypeNotBetween(String value1, String value2) {
            addCriterion("assignee_type not between", value1, value2, "assigneeType");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueIsNull() {
            addCriterion("assignee_value is null");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueIsNotNull() {
            addCriterion("assignee_value is not null");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueEqualTo(String value) {
            addCriterion("assignee_value =", value, "assigneeValue");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueNotEqualTo(String value) {
            addCriterion("assignee_value <>", value, "assigneeValue");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueGreaterThan(String value) {
            addCriterion("assignee_value >", value, "assigneeValue");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueGreaterThanOrEqualTo(String value) {
            addCriterion("assignee_value >=", value, "assigneeValue");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueLessThan(String value) {
            addCriterion("assignee_value <", value, "assigneeValue");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueLessThanOrEqualTo(String value) {
            addCriterion("assignee_value <=", value, "assigneeValue");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueLike(String value) {
            addCriterion("assignee_value like", value, "assigneeValue");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueNotLike(String value) {
            addCriterion("assignee_value not like", value, "assigneeValue");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueIn(List<String> values) {
            addCriterion("assignee_value in", values, "assigneeValue");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueNotIn(List<String> values) {
            addCriterion("assignee_value not in", values, "assigneeValue");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueBetween(String value1, String value2) {
            addCriterion("assignee_value between", value1, value2, "assigneeValue");
            return (Criteria) this;
        }

        public Criteria andAssigneeValueNotBetween(String value1, String value2) {
            addCriterion("assignee_value not between", value1, value2, "assigneeValue");
            return (Criteria) this;
        }

        public Criteria andAssigneeIsNull() {
            addCriterion("assignee is null");
            return (Criteria) this;
        }

        public Criteria andAssigneeIsNotNull() {
            addCriterion("assignee is not null");
            return (Criteria) this;
        }

        public Criteria andAssigneeEqualTo(String value) {
            addCriterion("assignee =", value, "assignee");
            return (Criteria) this;
        }

        public Criteria andAssigneeNotEqualTo(String value) {
            addCriterion("assignee <>", value, "assignee");
            return (Criteria) this;
        }

        public Criteria andAssigneeGreaterThan(String value) {
            addCriterion("assignee >", value, "assignee");
            return (Criteria) this;
        }

        public Criteria andAssigneeGreaterThanOrEqualTo(String value) {
            addCriterion("assignee >=", value, "assignee");
            return (Criteria) this;
        }

        public Criteria andAssigneeLessThan(String value) {
            addCriterion("assignee <", value, "assignee");
            return (Criteria) this;
        }

        public Criteria andAssigneeLessThanOrEqualTo(String value) {
            addCriterion("assignee <=", value, "assignee");
            return (Criteria) this;
        }

        public Criteria andAssigneeLike(String value) {
            addCriterion("assignee like", value, "assignee");
            return (Criteria) this;
        }

        public Criteria andAssigneeNotLike(String value) {
            addCriterion("assignee not like", value, "assignee");
            return (Criteria) this;
        }

        public Criteria andAssigneeIn(List<String> values) {
            addCriterion("assignee in", values, "assignee");
            return (Criteria) this;
        }

        public Criteria andAssigneeNotIn(List<String> values) {
            addCriterion("assignee not in", values, "assignee");
            return (Criteria) this;
        }

        public Criteria andAssigneeBetween(String value1, String value2) {
            addCriterion("assignee between", value1, value2, "assignee");
            return (Criteria) this;
        }

        public Criteria andAssigneeNotBetween(String value1, String value2) {
            addCriterion("assignee not between", value1, value2, "assignee");
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

        public Criteria andPermissionModeIsNull() {
            addCriterion("permission_mode is null");
            return (Criteria) this;
        }

        public Criteria andPermissionModeIsNotNull() {
            addCriterion("permission_mode is not null");
            return (Criteria) this;
        }

        public Criteria andPermissionModeEqualTo(String value) {
            addCriterion("permission_mode =", value, "permissionMode");
            return (Criteria) this;
        }

        public Criteria andPermissionModeNotEqualTo(String value) {
            addCriterion("permission_mode <>", value, "permissionMode");
            return (Criteria) this;
        }

        public Criteria andPermissionModeGreaterThan(String value) {
            addCriterion("permission_mode >", value, "permissionMode");
            return (Criteria) this;
        }

        public Criteria andPermissionModeGreaterThanOrEqualTo(String value) {
            addCriterion("permission_mode >=", value, "permissionMode");
            return (Criteria) this;
        }

        public Criteria andPermissionModeLessThan(String value) {
            addCriterion("permission_mode <", value, "permissionMode");
            return (Criteria) this;
        }

        public Criteria andPermissionModeLessThanOrEqualTo(String value) {
            addCriterion("permission_mode <=", value, "permissionMode");
            return (Criteria) this;
        }

        public Criteria andPermissionModeLike(String value) {
            addCriterion("permission_mode like", value, "permissionMode");
            return (Criteria) this;
        }

        public Criteria andPermissionModeNotLike(String value) {
            addCriterion("permission_mode not like", value, "permissionMode");
            return (Criteria) this;
        }

        public Criteria andPermissionModeIn(List<String> values) {
            addCriterion("permission_mode in", values, "permissionMode");
            return (Criteria) this;
        }

        public Criteria andPermissionModeNotIn(List<String> values) {
            addCriterion("permission_mode not in", values, "permissionMode");
            return (Criteria) this;
        }

        public Criteria andPermissionModeBetween(String value1, String value2) {
            addCriterion("permission_mode between", value1, value2, "permissionMode");
            return (Criteria) this;
        }

        public Criteria andPermissionModeNotBetween(String value1, String value2) {
            addCriterion("permission_mode not between", value1, value2, "permissionMode");
            return (Criteria) this;
        }

        public Criteria andModuleIsNull() {
            addCriterion("module is null");
            return (Criteria) this;
        }

        public Criteria andModuleIsNotNull() {
            addCriterion("module is not null");
            return (Criteria) this;
        }

        public Criteria andModuleEqualTo(String value) {
            addCriterion("module =", value, "module");
            return (Criteria) this;
        }

        public Criteria andModuleNotEqualTo(String value) {
            addCriterion("module <>", value, "module");
            return (Criteria) this;
        }

        public Criteria andModuleGreaterThan(String value) {
            addCriterion("module >", value, "module");
            return (Criteria) this;
        }

        public Criteria andModuleGreaterThanOrEqualTo(String value) {
            addCriterion("module >=", value, "module");
            return (Criteria) this;
        }

        public Criteria andModuleLessThan(String value) {
            addCriterion("module <", value, "module");
            return (Criteria) this;
        }

        public Criteria andModuleLessThanOrEqualTo(String value) {
            addCriterion("module <=", value, "module");
            return (Criteria) this;
        }

        public Criteria andModuleLike(String value) {
            addCriterion("module like", value, "module");
            return (Criteria) this;
        }

        public Criteria andModuleNotLike(String value) {
            addCriterion("module not like", value, "module");
            return (Criteria) this;
        }

        public Criteria andModuleIn(List<String> values) {
            addCriterion("module in", values, "module");
            return (Criteria) this;
        }

        public Criteria andModuleNotIn(List<String> values) {
            addCriterion("module not in", values, "module");
            return (Criteria) this;
        }

        public Criteria andModuleBetween(String value1, String value2) {
            addCriterion("module between", value1, value2, "module");
            return (Criteria) this;
        }

        public Criteria andModuleNotBetween(String value1, String value2) {
            addCriterion("module not between", value1, value2, "module");
            return (Criteria) this;
        }

        public Criteria andSqlCriterion(String value) {
            addCriterion("(" + value + ")");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table flow_link_value
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
     * This class corresponds to the database table flow_link_value
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