package com.example.adwebsite.mapper;

import com.example.adwebsite.dto.AdImage;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdImageMapper {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    public AdImageMapper(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 根据前缀随机查图
    public List<AdImage> findByImagePrefix(String prefix) {
        String sql = "SELECT * FROM ad_images " +
                "WHERE is_active = true " +
                "AND image_url LIKE :likePattern";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("likePattern", "/uploads/images/" + prefix + "_%.jpg");
        return jdbcTemplate.query(sql, params, new AdImageRowMapper());
    }

    // 浏览量+1
    public void incrementViews(Integer id) {
        jdbcTemplate.update("UPDATE ad_images SET views = views + 1 WHERE id = :id",
                new MapSqlParameterSource("id", id));
    }

    // 点击量+1
    public void incrementClicks(Integer id) {
        jdbcTemplate.update("UPDATE ad_images SET clicks = clicks + 1 WHERE id = :id",
                new MapSqlParameterSource("id", id));
    }

    private static class AdImageRowMapper implements RowMapper<AdImage> {
        @Override
        public AdImage mapRow(ResultSet rs, int rowNum) throws SQLException {
            AdImage img = new AdImage();
            img.setId(rs.getInt("id"));
            img.setAdType("image");
            img.setTitle(rs.getString("title"));
            img.setDescription(rs.getString("description"));
            img.setImageUrl(rs.getString("image_url"));
            img.setViews(rs.getInt("views"));
            img.setClicks(rs.getInt("clicks"));
            img.setIsActive(rs.getBoolean("is_active"));
            img.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            img.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return img;
        }
    }
}