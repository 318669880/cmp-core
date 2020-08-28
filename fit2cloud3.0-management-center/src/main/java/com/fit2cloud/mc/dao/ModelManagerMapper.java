package com.fit2cloud.mc.dao;

import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.model.ModelManagerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ModelManagerMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    long countByExample(ModelManagerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    int deleteByExample(ModelManagerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String uuid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    int insert(ModelManager record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    int insertSelective(ModelManager record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    List<ModelManager> selectByExampleWithBLOBs(ModelManagerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    List<ModelManager> selectByExample(ModelManagerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    ModelManager selectByPrimaryKey(String uuid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") ModelManager record, @Param("example") ModelManagerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("record") ModelManager record, @Param("example") ModelManagerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") ModelManager record, @Param("example") ModelManagerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ModelManager record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(ModelManager record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model_manager
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(ModelManager record);
}