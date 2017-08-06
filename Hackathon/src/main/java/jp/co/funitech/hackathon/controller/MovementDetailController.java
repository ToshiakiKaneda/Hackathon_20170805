package jp.co.funitech.hackathon.controller;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jp.co.funitech.hackathon.dao.TblnameDao;
import jp.co.funitech.hackathon.dto.TblnameDto;
import jp.co.funitech.hackathon.factory.SessionFactory;
import jp.co.funitech.hackathon.response.MovementDetailResponse;

@RestController
public class MovementDetailController {
  
  /** サンプルテーブルDao */
  private TblnameDao tblnameDao = new TblnameDao();

  @RequestMapping(value = "/movementDetail/{vegetableName}/{regionId}/{forecastMonth}", method = RequestMethod.GET)
  public MovementDetailResponse get(@PathVariable String vegetableName, @PathVariable int regionId, @PathVariable String forecastMonth) {
    MovementDetailResponse response = getMock();

    SqlSession sqlSession = SessionFactory.getInstance().getSession();
    List<TblnameDto> dtoList = tblnameDao.getList(sqlSession, new TblnameDto());
    for (TblnameDto dto : dtoList) {
      System.out.println(dto.regionId + ":" + dto.regionName);
    }
    
    response.vegetableName = vegetableName;
    response.regionId = regionId;
    response.forecastMonth = forecastMonth;
    return response;
  }
  
  private MovementDetailResponse getMock() {
    MovementDetailResponse mockResponse = new MovementDetailResponse();
    mockResponse.forecastMonth = "201709";
    mockResponse.basicDate = "20170805";
    mockResponse.vegetableName = "トマト";
    mockResponse.rateOfMovement = "0.10";
    mockResponse.regionId = 1;
    mockResponse.regionName = "北海道";
    mockResponse.detailInfo.add(createDetailInfo("温度の上昇", "基準年の温度より5度上昇で収穫量DOWN", "1"));
    mockResponse.detailInfo.add(createDetailInfo("14日ゲリラ豪雨の被害発生", "", "2"));
    mockResponse.detailInfo.add(createDetailInfo("トマト", "", "1"));
    return mockResponse;
  }
  
  private MovementDetailResponse.DetailInfo createDetailInfo(String title, String detail, String division) {
    MovementDetailResponse.DetailInfo detailInfo = new MovementDetailResponse.DetailInfo();
    detailInfo.title = title;
    detailInfo.detail = detail;
    detailInfo.division = division;
    return detailInfo;
  }
}
