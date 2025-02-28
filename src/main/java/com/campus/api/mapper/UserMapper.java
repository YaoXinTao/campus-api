package com.campus.api.mapper;

import com.campus.api.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("<script>" +
            "SELECT * FROM users " +
            "<where>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "   AND (nickname LIKE CONCAT('%',#{keyword},'%') " +
            "   OR phone LIKE CONCAT('%',#{keyword},'%') " +
            "   OR student_id LIKE CONCAT('%',#{keyword},'%') " +
            "   OR real_name LIKE CONCAT('%',#{keyword},'%'))" +
            "</if>" +
            "</where>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<User> selectList(@Param("keyword") String keyword, 
                         @Param("offset") Integer offset, 
                         @Param("pageSize") Integer pageSize);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM users " +
            "<where>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "   AND (nickname LIKE CONCAT('%',#{keyword},'%') " +
            "   OR phone LIKE CONCAT('%',#{keyword},'%') " +
            "   OR student_id LIKE CONCAT('%',#{keyword},'%') " +
            "   OR real_name LIKE CONCAT('%',#{keyword},'%'))" +
            "</if>" +
            "</where>" +
            "</script>")
    long selectCount(@Param("keyword") String keyword);
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    User selectById(Long id);
    
    @Select("SELECT * FROM users WHERE openid = #{openid}")
    User selectByOpenid(@Param("openid") String openid);
    
    @Insert("INSERT INTO users (openid, nickname, avatar_url, gender, phone, student_id, " +
            "real_name, status, last_login_time, last_login_ip) " +
            "VALUES (#{openid}, #{nickname}, #{avatarUrl}, #{gender}, #{phone}, #{studentId}, " +
            "#{realName}, #{status}, #{lastLoginTime}, #{lastLoginIp})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
    
    @Update({
        "<script>",
        "UPDATE users",
        "<set>",
        "  <if test='nickname != null'>nickname = #{nickname},</if>",
        "  <if test='avatarUrl != null'>avatar_url = #{avatarUrl},</if>",
        "  <if test='gender != null'>gender = #{gender},</if>",
        "  <if test='phone != null'>phone = #{phone},</if>",
        "  <if test='studentId != null'>student_id = #{studentId},</if>",
        "  <if test='realName != null'>real_name = #{realName},</if>",
        "  <if test='status != null'>status = #{status},</if>",
        "  <if test='lastLoginTime != null'>last_login_time = #{lastLoginTime},</if>",
        "  <if test='lastLoginIp != null'>last_login_ip = #{lastLoginIp},</if>",
        "  updated_at = CURRENT_TIMESTAMP",
        "</set>",
        "WHERE id = #{id}",
        "</script>"
    })
    void update(User user);
    
    @Update("UPDATE users SET student_id = #{studentId}, real_name = #{realName}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE id = #{userId}")
    void updateStudentInfo(@Param("userId") Long userId,
                          @Param("studentId") String studentId,
                          @Param("realName") String realName);

    @Select("SELECT * FROM users WHERE phone = #{phone}")
    User selectByPhone(@Param("phone") String phone);
    
    @Select("SELECT * FROM users WHERE student_id = #{studentId}")
    User selectByStudentId(@Param("studentId") String studentId);
    
    @Update("UPDATE users SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{userId}")
    int updateStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    @Update("UPDATE users SET password = #{password}, updated_at = CURRENT_TIMESTAMP WHERE id = #{userId}")
    void updatePassword(@Param("userId") Long userId, @Param("password") String password);
} 