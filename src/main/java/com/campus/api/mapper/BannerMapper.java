package com.campus.api.mapper;

import com.campus.api.entity.Banner;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BannerMapper {

    @Insert("INSERT INTO banners(title, image_url, link_type, link_value, position, sort_order, " +
            "start_time, end_time, status, remark, created_by, created_at, updated_at) " +
            "VALUES(#{title}, #{imageUrl}, #{linkType}, #{linkValue}, #{position}, #{sortOrder}, " +
            "#{startTime}, #{endTime}, #{status}, #{remark}, #{createdBy}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Banner banner);

    @Update("UPDATE banners SET title = #{title}, image_url = #{imageUrl}, link_type = #{linkType}, " +
            "link_value = #{linkValue}, position = #{position}, sort_order = #{sortOrder}, " +
            "start_time = #{startTime}, end_time = #{endTime}, status = #{status}, " +
            "remark = #{remark}, updated_at = #{updatedAt} WHERE id = #{id}")
    void update(Banner banner);

    @Update("UPDATE banners SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM banners WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM banners WHERE id = #{id}")
    Banner selectById(Long id);

    @Select("<script>" +
            "SELECT * FROM banners WHERE 1=1" +
            "<if test='position != null and position != \"\"'> AND position = #{position}</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            " ORDER BY sort_order ASC, id DESC" +
            "</script>")
    List<Banner> selectList(@Param("position") String position, @Param("status") Integer status);

    @Select("SELECT * FROM banners WHERE status = 1 AND position = #{position} " +
            "AND (start_time IS NULL OR start_time <= NOW()) " +
            "AND (end_time IS NULL OR end_time >= NOW()) " +
            "ORDER BY sort_order ASC, id DESC")
    List<Banner> selectActiveBanners(String position);
} 