package com.fit2cloud.mc.dao;

import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelBasicExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ModelBasicMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    long countByExample(ModelBasicExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    int deleteByExample(ModelBasicExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String modelUuid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    int insert(ModelBasic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    int insertSelective(ModelBasic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    List<ModelBasic> selectByExampleWithBLOBs(ModelBasicExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    List<ModelBasic> selectByExample(ModelBasicExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    ModelBasic selectByPrimaryKey(String modelUuid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") ModelBasic record, @Param("example") ModelBasicExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("record") ModelBasic record, @Param("example") ModelBasicExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") ModelBasic record, @Param("example") ModelBasicExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ModelBasic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(ModelBasic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(ModelBasic record);
}