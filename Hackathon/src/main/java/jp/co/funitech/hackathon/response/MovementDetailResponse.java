package jp.co.funitech.hackathon.response;

import java.util.ArrayList;
import java.util.List;

public class MovementDetailResponse {
  public String forecastMonth;
  
  public String basicDate;
  
  public String vegetableName;
  
  public String rateOfMovement;
  
  public int regionId;
  
  public String regionName;
  
  public List<DetailInfo> detailInfo = new ArrayList<>();
  
  public static class DetailInfo {
    public String title;
    
    public String detail;
    
    public String division;
  }
}
