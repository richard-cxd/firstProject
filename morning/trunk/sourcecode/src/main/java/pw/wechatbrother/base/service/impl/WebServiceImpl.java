package pw.wechatbrother.base.service.impl;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.wechatbrother.base.mapper.WebServiceMapper;
import pw.wechatbrother.base.service.WebServiceService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YN on 2018/1/22.
 */
@Service
public class WebServiceImpl implements WebServiceService{
    @Resource
    private WebServiceMapper webServiceMapper;


    @Override
    public JSONObject findData(String companyId,String monitorId) {
        //封装一个监测点的信息
        String lastDataTag = webServiceMapper.findLastDataTag(monitorId);
        String maxDataData = webServiceMapper.findMaxDataData(companyId, monitorId);
        String AX = "AX_"+lastDataTag;
        String BX = "BX_"+lastDataTag;
        String CX = "CX_"+lastDataTag;
        List<HashMap> data = webServiceMapper.findData(AX, BX, CX,companyId,monitorId);
        JSONObject dataJson = new JSONObject();
        dataJson.put("terminalId",monitorId);
        dataJson.put("getTime",maxDataData+lastDataTag+"00");
        for (int i =0;i<data.size();i++){
            Map<String,String> map= data.get(i);
                if("1".equals(String.valueOf(map.get("type")))){
                    //有功
                    dataJson.put("02",map.get(AX));
                    dataJson.put("03",map.get(BX));
                    dataJson.put("04",map.get(CX));
                }
                else if("2".equals(String.valueOf(map.get("type")))){
                    //无功
                    dataJson.put("06",map.get(AX));
                    dataJson.put("07",map.get(BX));
                    dataJson.put("08",map.get(CX));
                }
                else if("3".equals(String.valueOf(map.get("type")))){
                    //电流
                    dataJson.put("13",map.get(AX));
                    dataJson.put("14",map.get(BX));
                    dataJson.put("15",map.get(CX));
                }
                else if("4".equals(String.valueOf(map.get("type")))){
                    //电压
                    dataJson.put("17",map.get(AX));
                    dataJson.put("18",map.get(BX));
                    dataJson.put("19",map.get(CX));
                }


        }
        return dataJson;
    }
}
