package com.campus.api.mapper;

import com.campus.api.entity.AdminUser;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AdminUserMapper {
    /**
     * 根据ID查询管理员
     */
    @Select("SELECT * FROM admin_users WHERE id = #{id}")
    AdminUser selectById(@Param("id") Long id);

    /**
     * 根据用户名查询管理员
     */
    @Select("SELECT * FROM admin_users WHERE username = #{username}")
    AdminUser selectByUsername(@Param("username") String username);

    /**
     * 查询管理员列表
     */
    @Select("SELECT * FROM admin_users ORDER BY id DESC")
    List<AdminUser> selectList();

    /**
     * 插入管理员
     */
    @Insert("INSERT INTO admin_users (username, password, real_name, phone, email, " +
            "avatar_url, role, department, status, created_by) " +
            "VALUES (#{username}, #{password}, #{realName}, #{phone}, #{email}, " +
            "#{avatarUrl}, #{role}, #{department}, #{status}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AdminUser adminUser);

    /**
     * 更新管理员信息
     */
    @Update({
        "<script>",
        "UPDATE admin_users",
        "<set>",
        "  <if test='username != null'>username = #{username},</if>",
        "  <if test='realName != null'>real_name = #{realName},</if>",
        "  <if test='phone != null'>phone = #{phone},</if>",
        "  <if test='email != null'>email = #{email},</if>",
        "  <if test='avatarUrl != null'>avatar_url = #{avatarUrl},</if>",
        "  <if test='role != null'>role = #{role},</if>",
        "  <if test='department != null'>department = #{department},</if>",
        "  <if test='status != null'>status = #{status},</if>",
        "  <if test='lastLoginTime != null'>last_login_time = #{lastLoginTime},</if>",
        "  <if test='lastLoginIp != null'>last_login_ip = #{lastLoginIp},</if>",
        "  updated_at = CURRENT_TIMESTAMP",
        "</set>",
        "WHERE id = #{id}",
        "</script>"
    })
    int update(AdminUser adminUser);

    /**
     * 删除管理员
     */
    @Delete("DELETE FROM admin_users WHERE id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 更新密码
     */
    @Update("UPDATE admin_users SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 更新状态
     */
    @Update("UPDATE admin_users SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
} 