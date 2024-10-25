package com.google.backend.trading.framework.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author trading
 */
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonTypeHandler extends BaseTypeHandler<PGobject> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PGobject parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    @Override
    public PGobject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object obj = rs.getObject(columnName);
        if (obj instanceof PGobject) {
            return (PGobject) obj;
        }
        throw new RuntimeException("JdbcType.OTHER property is not PgObject instance");
    }

    @Override
    public PGobject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object obj = rs.getObject(columnIndex);
        if (obj instanceof PGobject) {
            return (PGobject) obj;
        }
        throw new RuntimeException("JdbcType.OTHER property is not PgObject instance");
    }

    @Override
    public PGobject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object obj = cs.getObject(columnIndex);
        if (obj instanceof PGobject) {
            return (PGobject) obj;
        }
        throw new RuntimeException("JdbcType.OTHER property is not PgObject instance");
    }
}