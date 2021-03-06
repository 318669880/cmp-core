package com.fit2cloud.mc.model;

import java.util.ArrayList;
import java.util.List;

public class ModelVersionExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table model_version
     *
     * @mbg.generated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table model_version
     *
     * @mbg.generated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table model_version
     *
     * @mbg.generated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_version
     *
     * @mbg.generated
     */
    public ModelVersionExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_version
     *
     * @mbg.generated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_version
     *
     * @mbg.generated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_version
     *
     * @mbg.generated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_version
     *
     * @mbg.generated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_version
     *
     * @mbg.generated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_version
     *
     * @mbg.generated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_version
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
     * This method corresponds to the database table model_version
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
     * This method corresponds to the database table model_version
     *
     * @mbg.generated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_version
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
     * This class corresponds to the database table model_version
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

        public Criteria andModelVersionUuidIsNull() {
            addCriterion("model_version_uuid is null");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidIsNotNull() {
            addCriterion("model_version_uuid is not null");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidEqualTo(String value) {
            addCriterion("model_version_uuid =", value, "modelVersionUuid");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidNotEqualTo(String value) {
            addCriterion("model_version_uuid <>", value, "modelVersionUuid");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidGreaterThan(String value) {
            addCriterion("model_version_uuid >", value, "modelVersionUuid");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidGreaterThanOrEqualTo(String value) {
            addCriterion("model_version_uuid >=", value, "modelVersionUuid");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidLessThan(String value) {
            addCriterion("model_version_uuid <", value, "modelVersionUuid");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidLessThanOrEqualTo(String value) {
            addCriterion("model_version_uuid <=", value, "modelVersionUuid");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidLike(String value) {
            addCriterion("model_version_uuid like", value, "modelVersionUuid");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidNotLike(String value) {
            addCriterion("model_version_uuid not like", value, "modelVersionUuid");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidIn(List<String> values) {
            addCriterion("model_version_uuid in", values, "modelVersionUuid");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidNotIn(List<String> values) {
            addCriterion("model_version_uuid not in", values, "modelVersionUuid");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidBetween(String value1, String value2) {
            addCriterion("model_version_uuid between", value1, value2, "modelVersionUuid");
            return (Criteria) this;
        }

        public Criteria andModelVersionUuidNotBetween(String value1, String value2) {
            addCriterion("model_version_uuid not between", value1, value2, "modelVersionUuid");
            return (Criteria) this;
        }

        public Criteria andCreatedIsNull() {
            addCriterion("created is null");
            return (Criteria) this;
        }

        public Criteria andCreatedIsNotNull() {
            addCriterion("created is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedEqualTo(Long value) {
            addCriterion("created =", value, "created");
            return (Criteria) this;
        }

        public Criteria andCreatedNotEqualTo(Long value) {
            addCriterion("created <>", value, "created");
            return (Criteria) this;
        }

        public Criteria andCreatedGreaterThan(Long value) {
            addCriterion("created >", value, "created");
            return (Criteria) this;
        }

        public Criteria andCreatedGreaterThanOrEqualTo(Long value) {
            addCriterion("created >=", value, "created");
            return (Criteria) this;
        }

        public Criteria andCreatedLessThan(Long value) {
            addCriterion("created <", value, "created");
            return (Criteria) this;
        }

        public Criteria andCreatedLessThanOrEqualTo(Long value) {
            addCriterion("created <=", value, "created");
            return (Criteria) this;
        }

        public Criteria andCreatedIn(List<Long> values) {
            addCriterion("created in", values, "created");
            return (Criteria) this;
        }

        public Criteria andCreatedNotIn(List<Long> values) {
            addCriterion("created not in", values, "created");
            return (Criteria) this;
        }

        public Criteria andCreatedBetween(Long value1, Long value2) {
            addCriterion("created between", value1, value2, "created");
            return (Criteria) this;
        }

        public Criteria andCreatedNotBetween(Long value1, Long value2) {
            addCriterion("created not between", value1, value2, "created");
            return (Criteria) this;
        }

        public Criteria andDesctiptionIsNull() {
            addCriterion("desctiption is null");
            return (Criteria) this;
        }

        public Criteria andDesctiptionIsNotNull() {
            addCriterion("desctiption is not null");
            return (Criteria) this;
        }

        public Criteria andDesctiptionEqualTo(String value) {
            addCriterion("desctiption =", value, "desctiption");
            return (Criteria) this;
        }

        public Criteria andDesctiptionNotEqualTo(String value) {
            addCriterion("desctiption <>", value, "desctiption");
            return (Criteria) this;
        }

        public Criteria andDesctiptionGreaterThan(String value) {
            addCriterion("desctiption >", value, "desctiption");
            return (Criteria) this;
        }

        public Criteria andDesctiptionGreaterThanOrEqualTo(String value) {
            addCriterion("desctiption >=", value, "desctiption");
            return (Criteria) this;
        }

        public Criteria andDesctiptionLessThan(String value) {
            addCriterion("desctiption <", value, "desctiption");
            return (Criteria) this;
        }

        public Criteria andDesctiptionLessThanOrEqualTo(String value) {
            addCriterion("desctiption <=", value, "desctiption");
            return (Criteria) this;
        }

        public Criteria andDesctiptionLike(String value) {
            addCriterion("desctiption like", value, "desctiption");
            return (Criteria) this;
        }

        public Criteria andDesctiptionNotLike(String value) {
            addCriterion("desctiption not like", value, "desctiption");
            return (Criteria) this;
        }

        public Criteria andDesctiptionIn(List<String> values) {
            addCriterion("desctiption in", values, "desctiption");
            return (Criteria) this;
        }

        public Criteria andDesctiptionNotIn(List<String> values) {
            addCriterion("desctiption not in", values, "desctiption");
            return (Criteria) this;
        }

        public Criteria andDesctiptionBetween(String value1, String value2) {
            addCriterion("desctiption between", value1, value2, "desctiption");
            return (Criteria) this;
        }

        public Criteria andDesctiptionNotBetween(String value1, String value2) {
            addCriterion("desctiption not between", value1, value2, "desctiption");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlIsNull() {
            addCriterion("download_url is null");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlIsNotNull() {
            addCriterion("download_url is not null");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlEqualTo(String value) {
            addCriterion("download_url =", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlNotEqualTo(String value) {
            addCriterion("download_url <>", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlGreaterThan(String value) {
            addCriterion("download_url >", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlGreaterThanOrEqualTo(String value) {
            addCriterion("download_url >=", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlLessThan(String value) {
            addCriterion("download_url <", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlLessThanOrEqualTo(String value) {
            addCriterion("download_url <=", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlLike(String value) {
            addCriterion("download_url like", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlNotLike(String value) {
            addCriterion("download_url not like", value, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlIn(List<String> values) {
            addCriterion("download_url in", values, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlNotIn(List<String> values) {
            addCriterion("download_url not in", values, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlBetween(String value1, String value2) {
            addCriterion("download_url between", value1, value2, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlNotBetween(String value1, String value2) {
            addCriterion("download_url not between", value1, value2, "downloadUrl");
            return (Criteria) this;
        }

        public Criteria andRevisionIsNull() {
            addCriterion("revision is null");
            return (Criteria) this;
        }

        public Criteria andRevisionIsNotNull() {
            addCriterion("revision is not null");
            return (Criteria) this;
        }

        public Criteria andRevisionEqualTo(String value) {
            addCriterion("revision =", value, "revision");
            return (Criteria) this;
        }

        public Criteria andRevisionNotEqualTo(String value) {
            addCriterion("revision <>", value, "revision");
            return (Criteria) this;
        }

        public Criteria andRevisionGreaterThan(String value) {
            addCriterion("revision >", value, "revision");
            return (Criteria) this;
        }

        public Criteria andRevisionGreaterThanOrEqualTo(String value) {
            addCriterion("revision >=", value, "revision");
            return (Criteria) this;
        }

        public Criteria andRevisionLessThan(String value) {
            addCriterion("revision <", value, "revision");
            return (Criteria) this;
        }

        public Criteria andRevisionLessThanOrEqualTo(String value) {
            addCriterion("revision <=", value, "revision");
            return (Criteria) this;
        }

        public Criteria andRevisionLike(String value) {
            addCriterion("revision like", value, "revision");
            return (Criteria) this;
        }

        public Criteria andRevisionNotLike(String value) {
            addCriterion("revision not like", value, "revision");
            return (Criteria) this;
        }

        public Criteria andRevisionIn(List<String> values) {
            addCriterion("revision in", values, "revision");
            return (Criteria) this;
        }

        public Criteria andRevisionNotIn(List<String> values) {
            addCriterion("revision not in", values, "revision");
            return (Criteria) this;
        }

        public Criteria andRevisionBetween(String value1, String value2) {
            addCriterion("revision between", value1, value2, "revision");
            return (Criteria) this;
        }

        public Criteria andRevisionNotBetween(String value1, String value2) {
            addCriterion("revision not between", value1, value2, "revision");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidIsNull() {
            addCriterion("model_basic_uuid is null");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidIsNotNull() {
            addCriterion("model_basic_uuid is not null");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidEqualTo(String value) {
            addCriterion("model_basic_uuid =", value, "modelBasicUuid");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidNotEqualTo(String value) {
            addCriterion("model_basic_uuid <>", value, "modelBasicUuid");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidGreaterThan(String value) {
            addCriterion("model_basic_uuid >", value, "modelBasicUuid");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidGreaterThanOrEqualTo(String value) {
            addCriterion("model_basic_uuid >=", value, "modelBasicUuid");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidLessThan(String value) {
            addCriterion("model_basic_uuid <", value, "modelBasicUuid");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidLessThanOrEqualTo(String value) {
            addCriterion("model_basic_uuid <=", value, "modelBasicUuid");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidLike(String value) {
            addCriterion("model_basic_uuid like", value, "modelBasicUuid");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidNotLike(String value) {
            addCriterion("model_basic_uuid not like", value, "modelBasicUuid");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidIn(List<String> values) {
            addCriterion("model_basic_uuid in", values, "modelBasicUuid");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidNotIn(List<String> values) {
            addCriterion("model_basic_uuid not in", values, "modelBasicUuid");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidBetween(String value1, String value2) {
            addCriterion("model_basic_uuid between", value1, value2, "modelBasicUuid");
            return (Criteria) this;
        }

        public Criteria andModelBasicUuidNotBetween(String value1, String value2) {
            addCriterion("model_basic_uuid not between", value1, value2, "modelBasicUuid");
            return (Criteria) this;
        }

        public Criteria andInstallTimeIsNull() {
            addCriterion("install_time is null");
            return (Criteria) this;
        }

        public Criteria andInstallTimeIsNotNull() {
            addCriterion("install_time is not null");
            return (Criteria) this;
        }

        public Criteria andInstallTimeEqualTo(Long value) {
            addCriterion("install_time =", value, "installTime");
            return (Criteria) this;
        }

        public Criteria andInstallTimeNotEqualTo(Long value) {
            addCriterion("install_time <>", value, "installTime");
            return (Criteria) this;
        }

        public Criteria andInstallTimeGreaterThan(Long value) {
            addCriterion("install_time >", value, "installTime");
            return (Criteria) this;
        }

        public Criteria andInstallTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("install_time >=", value, "installTime");
            return (Criteria) this;
        }

        public Criteria andInstallTimeLessThan(Long value) {
            addCriterion("install_time <", value, "installTime");
            return (Criteria) this;
        }

        public Criteria andInstallTimeLessThanOrEqualTo(Long value) {
            addCriterion("install_time <=", value, "installTime");
            return (Criteria) this;
        }

        public Criteria andInstallTimeIn(List<Long> values) {
            addCriterion("install_time in", values, "installTime");
            return (Criteria) this;
        }

        public Criteria andInstallTimeNotIn(List<Long> values) {
            addCriterion("install_time not in", values, "installTime");
            return (Criteria) this;
        }

        public Criteria andInstallTimeBetween(Long value1, Long value2) {
            addCriterion("install_time between", value1, value2, "installTime");
            return (Criteria) this;
        }

        public Criteria andInstallTimeNotBetween(Long value1, Long value2) {
            addCriterion("install_time not between", value1, value2, "installTime");
            return (Criteria) this;
        }

        public Criteria andSqlCriterion(String value) {
            addCriterion("(" + value + ")");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table model_version
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
     * This class corresponds to the database table model_version
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