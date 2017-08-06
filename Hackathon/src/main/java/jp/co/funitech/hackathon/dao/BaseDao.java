package jp.co.funitech.hackathon.dao;

import java.util.List;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.funitech.hackathon.util.ExStringUtils;

/**
 * DAOの基底クラス
 * 
 * @author takashi.onoue
 *
 */
public class BaseDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseDao.class);

  /**
   * SELECTを実行して該当データのリストを取得
   * 
   * @param targetId
   * @param parameter
   * @return
   */
  public <T> List<T> getList(SqlSession sqlSession, String targetId, Object parameter) {
    logQuery(sqlSession, targetId, parameter);
    return sqlSession.selectList(targetId, parameter);
  }

  /**
   * SELECTを実行して該当データを取得
   * 
   * @param sqlSession
   * @param targetId
   * @param parameter
   * @return
   */
  public <T> T getObject(SqlSession sqlSession, String targetId, Object parameter) {
    logQuery(sqlSession, targetId, parameter);
    return sqlSession.selectOne(targetId, parameter);
  }

  /**
   * SELECTを実行して数値を取得<br>
   * 該当なしの場合はNULLを返す
   * 
   * @param sqlSession
   * @param targetId
   * @param parameter
   * @return
   */
  public Integer getInteger(SqlSession sqlSession, String targetId, Object parameter) {
    logQuery(sqlSession, targetId, parameter);
    Object result = getObject(sqlSession, targetId, parameter);
    return result == null ? null : (Integer) result;
  }

  /**
   * SELECTを実行して文字列を取得<br>
   * 該当なしの場合はNULLを返す
   * 
   * @param sqlSession
   * @param targetId
   * @param parameter
   * @return
   * @throws Exception
   */
  public String getString(SqlSession sqlSession, String targetId, Object parameter) {
    logQuery(sqlSession, targetId, parameter);
    Object result = getObject(sqlSession, targetId, parameter);
    return result == null ? null : result.toString();
  }

  /**
   * デバッグのSQLを出力する
   * 
   * @param session
   * @param targetId
   * @param parameterObject
   */
  private void logQuery(SqlSession session, String targetId, Object parameterObject) {
    // To get the SQL statement to be executed
    String strQuery = this.getRunSql(session, targetId, parameterObject);
    // Output log
    this.logQuery(targetId, strQuery);
  }

  /**
   * デバッグのSQLを出力する
   * 
   * @param targetId
   * @param strQuery
   */
  private void logQuery(String targetId, String strQuery) {

    try {
      // 出力内容生成
      StringBuilder log = new StringBuilder();
      log.append("************************** START *******************************\n");
      log.append("SQL : " + targetId + "\n");
      log.append("QUERY : \n" + replaceQuery(strQuery) + "\n");
      log.append("************************** END *******************************");

      System.out.println(log.toString());
      LOGGER.debug(log.toString());
    } catch (Exception e) {
      LOGGER.debug("Failed print of debug sql", e.getMessage());
    }
  }

  /**
   * To get the SQL statement to be executed
   * 
   * @param session
   * @param targetId
   * @param parameterObject
   * @return SQL statement to be executed
   */
  private String getRunSql(SqlSession session, String targetId, Object parameterObject) {

    try {
      MappedStatement mappedStatement = session.getConfiguration().getMappedStatement(targetId);
      BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(parameterObject);
      String strQuery = boundSql.getSql();

      List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
      if (parameterMappings == null || parameterObject == null) {
        return replaceLineFeeAndSpace(strQuery);
      }

      Configuration configuration = mappedStatement.getConfiguration();
      TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
      MetaObject metaObject = configuration.newMetaObject(parameterObject);

      for (int i = 0; i < parameterMappings.size(); i++) {
        ParameterMapping parameterMapping = parameterMappings.get(i);
        Object value = null;
        if (parameterMapping.getMode() == ParameterMode.OUT) {
          continue;
        }
        String propertyName = parameterMapping.getProperty();
        PropertyTokenizer prop = new PropertyTokenizer(propertyName);
        if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
          value = parameterObject;
        } else if (boundSql.hasAdditionalParameter(propertyName)) {
          value = boundSql.getAdditionalParameter(propertyName);
        } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)
            && boundSql.hasAdditionalParameter(prop.getName())) {
          value = boundSql.getAdditionalParameter(prop.getName());
          if (value != null) {
            value = configuration.newMetaObject(value)
                .getValue(propertyName.substring(prop.getName().length()));
          }
        } else {
          value = metaObject == null ? null : metaObject.getValue(propertyName);
        }

        if (value != null) {
          if (parameterMapping.getJavaType().getTypeName().equals("java.lang.String")) {
            strQuery = strQuery.replaceFirst("\\?", "'" + String.valueOf(value) + "'");
          } else {
            strQuery = strQuery.replaceFirst("\\?", String.valueOf(value));
          }
        }
      }
      return replaceLineFeeAndSpace(strQuery);
    } catch (Exception e) {
      LOGGER.debug("実行SQLの生成に失敗", e);
      return ExStringUtils.EMPTY;
    }
  }

  /**
   * Query改行と空白を整形する
   * 
   * @param query
   * @return
   */
  private String replaceLineFeeAndSpace(String query) {
    String result = "";
    result = query;
    result = result.replaceAll("\t", " ");
    result = result.replaceAll("\n", " ");
    int i = 0;
    do {
      result = result.replaceAll("  ", " ");
      i++;
    } while (result.indexOf("  ") != -1 && i < 100); // continue to not exist double space or
                                                     // looping 100 count

    return result;
  }

  /**
   * SQLを整形する
   * 
   * @param query
   * @return
   */
  private String replaceQuery(String query) {
    String result = "";
    result = query;
    result = result.replaceAll(" ,", ",");
    result = result.replaceAll(", ", ",\n       ");
    result = result.replaceAll(" SELECT ", "\n SELECT ");
    result = result.replaceAll(" FROM ", "\n FROM ");
    result = result.replaceAll(" WHERE ", "\n WHERE ");
    result = result.replaceAll(" AND ", "\n AND ");
    result = result.replaceAll("ORDER BY", "\n ORDER BY");
    result = result.replaceAll("GROUP BY", "\n GROUP BY");
    result = result.replaceAll("HAVING ", "\n HAVING ");
    result = result.replaceAll("UNION ", "\n UNION ");
    result = result.replaceAll("VALUES", "\n VALUES");
    result = result.replaceAll(" LEFT ", "\n LEFT ");
    result = result.replaceAll(" INNER ", "\n INNER ");
    return result;
  }

}
