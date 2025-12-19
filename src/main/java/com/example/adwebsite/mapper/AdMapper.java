package com.example.adwebsite.mapper;

import com.example.adwebsite.entity.Advertisement;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdMapper {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AdMapper(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Advertisement> findAll() {
        String sql = "SELECT * FROM advertisements WHERE is_active = true";
        return jdbcTemplate.query(sql, new AdvertisementRowMapper());
    }

    public Advertisement findById(Integer id) {
        String sql = "SELECT * FROM advertisements WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        return jdbcTemplate.queryForObject(sql, params, new AdvertisementRowMapper());
    }

    public Integer insert(Advertisement ad) {
        String sql = "INSERT INTO advertisements (title, description, video_url, duration) " +
                "VALUES (:title, :description, :videoUrl, :duration)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", ad.getTitle())
                .addValue("description", ad.getDescription())
                .addValue("videoUrl", ad.getVideoUrl())
                .addValue("duration", ad.getDuration());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void update(Advertisement ad) {
        String sql = "UPDATE advertisements SET title = :title, description = :description, " +
                "video_url = :videoUrl, duration = :duration, is_active = :isActive " +
                "WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", ad.getId())
                .addValue("title", ad.getTitle())
                .addValue("description", ad.getDescription())
                .addValue("videoUrl", ad.getVideoUrl())
                .addValue("duration", ad.getDuration())
                .addValue("isActive", ad.getIsActive());

        jdbcTemplate.update(sql, params);
    }

    // 操作数据库内容
    public void delete(Integer id) {
        String sql = "DELETE FROM advertisements WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }

    public void incrementViews(Integer id) {
        String sql = "UPDATE advertisements SET views = views + 1 WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }

    public void incrementClicks(Integer id) {
        String sql = "UPDATE advertisements SET clicks = clicks + 1 WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }

    private static class AdvertisementRowMapper implements RowMapper<Advertisement> {
        @Override
        public Advertisement mapRow(ResultSet rs, int rowNum) throws SQLException {
            Advertisement ad = new Advertisement();
            ad.setId(rs.getInt("id"));
            ad.setTitle(rs.getString("title"));
            ad.setDescription(rs.getString("description"));
            ad.setVideoUrl(rs.getString("video_url"));
            ad.setDuration(rs.getInt("duration"));
            ad.setClicks(rs.getInt("clicks"));
            ad.setViews(rs.getInt("views"));
            ad.setIsActive(rs.getBoolean("is_active"));
            ad.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            ad.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return ad;
        }
    }
}