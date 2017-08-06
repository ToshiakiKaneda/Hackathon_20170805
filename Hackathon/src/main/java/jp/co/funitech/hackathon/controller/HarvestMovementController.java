package jp.co.funitech.hackathon.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import jp.co.funitech.hackathon.response.HarvestMovementResponse;

@RestController
public class HarvestMovementController {

  @RequestMapping(value = "/harvestMovement/{forecastMonth}", method = RequestMethod.GET)
  public HarvestMovementResponse get(@PathVariable String forecastMonth) {
    HarvestMovementResponse response = getMock();
    response.forecastMonth = forecastMonth;
    return response;
  }
  
  private HarvestMovementResponse getMock() {

    HarvestMovementResponse mockResponse = new HarvestMovementResponse();
    mockResponse.forecastMonth = "201709";
    mockResponse.basicDate = "20170805";
    mockResponse.vegetableInfo.add(createVegetableInfo("トマト", "0.10", 1, "北海道"));
    mockResponse.vegetableInfo.add(createVegetableInfo("トマト", "0.10", 1, "北海道"));
    mockResponse.vegetableInfo.add(createVegetableInfo("トマト", "0.10", 1, "北海道"));
    mockResponse.vegetableInfo.add(createVegetableInfo("にんじん", "0.10", 1, "北海道"));
    mockResponse.vegetableInfo.add(createVegetableInfo("にんじん", "0.10", 1, "北海道"));
    mockResponse.vegetableInfo.add(createVegetableInfo("にんじん", "0.10", 1, "北海道"));
    return mockResponse;
  }
  
  private HarvestMovementResponse.VegetableInfo createVegetableInfo(String vegetableName, String rateOfMovement, int regionId, String regionName) {
    HarvestMovementResponse.VegetableInfo vegetableInfo = new HarvestMovementResponse.VegetableInfo();
    vegetableInfo.vegetableName = vegetableName;
    vegetableInfo.rateOfMovement = rateOfMovement;
    vegetableInfo.regionId = regionId;
    vegetableInfo.regionName = regionName;
    return vegetableInfo;
  }
  
}
