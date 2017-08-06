package jp.co.funitech.hackathon.response;

import java.util.ArrayList;
import java.util.List;

public class HarvestMovementResponse {

  public String forecastMonth;

  public String basicDate;
  
  public List<VegetableInfo> vegetableInfo = new ArrayList<>();

  public static class VegetableInfo {
    
    public String vegetableName;
    
    public String rateOfMovement;
    
    public int regionId;
    
    public String regionName;
    
  }

}
