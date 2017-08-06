package jp.co.funitech.hackathon.factory;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SessionのFactoryクラス
 */
public class SessionFactory {

  /** log */
  private static final Logger LOGGER = LoggerFactory.getLogger(SessionFactory.class);

  /** 設定ファイル名 */
  private static final String RESOURCE_NAME = "mybatis-config.xml";

  /** singleton */
  private static final SessionFactory INSTANCE = new SessionFactory();

  /** DB session factory */
  private SqlSessionFactory sessionFactory;

  /**
   * new不可コンストラクタ
   * 
   * @throws IOException
   */
  private SessionFactory() {
    try {
      // rental のプール生成
      this.sessionFactory = getSessionFactory();

    } catch (Exception e) {
      throw new RuntimeException("SqlSessionFactoryの初期化に失敗", e);
    }

    // Factoryの生成チェック ※Factoryがひとつもない場合はエラーとする
    if (this.sessionFactory == null) {
      throw new RuntimeException("SqlSessionFactoryが生成されませんでした");
    }
  }

  /**
   * SqlSessionFactoryを生成
   * 
   * @param dataBase
   * @return
   */
  protected SqlSessionFactory getSessionFactory() {
    try {
      // Factoryを生成すると自動的にInputStreamはcloseされてしまうので、Factory生成毎に設定ファイルを読み込む
      InputStream inputStream = Resources.getResourceAsStream(RESOURCE_NAME);
      return new SqlSessionFactoryBuilder().build(inputStream, "hackathon");
    } catch (Exception e) {
      LOGGER.debug("SqlSessionFactoryは生成されませんでした。", e);
      return null;
    }
  }

  /**
   * singletonインスタンスを取得
   * 
   * @return
   */
  public static SessionFactory getInstance() {
    return INSTANCE;
  }
  /**
   * SqlSessionを取得<br>
   * コミットされるまで外から認識されないトランザクションレベルのSqlSessionを返す
   * 
   * @param dataBase
   * @return
   */
  public SqlSession getSession() {
    return getSqlSession(TransactionIsolationLevel.READ_COMMITTED);
  }

  /**
   * SqlSessionを取得
   * 
   * @param dataBase
   * @param level
   * @return
   */
  protected SqlSession getSqlSession(TransactionIsolationLevel level) {
       return getSqlSession(this.sessionFactory, level);
  }

  /**
   * SqlSessionを取得<br>
   * PreparedStatementを再利用するSqlSessionを返す<br>
   * SqlSessionFactoryが生成されていない場合はRentalDBFatalExceptionを返す
   * 
   * @param factory
   * @param level
   * @return
   */
  protected SqlSession getSqlSession(SqlSessionFactory factory, TransactionIsolationLevel level) {
    if (factory == null) {
      throw new RuntimeException("SqlSessionFactoryが生成されていません");
    }
    return factory.openSession(ExecutorType.REUSE, level);
  }
}
