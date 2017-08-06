package jp.co.funitech.hackathon.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import jp.co.funitech.hackathon.dto.TblnameDto;

public class TblnameDao extends BaseDao {
  /**
   * SELECT
   * 
   * @param sqlSession
   * @param query
   * @return
   * @throws Exception
   */
  public List<TblnameDto> getList(SqlSession sqlSession, TblnameDto dto) {
    return super.getList(sqlSession, "TBLNAME.getTblname", dto);
  }
}
