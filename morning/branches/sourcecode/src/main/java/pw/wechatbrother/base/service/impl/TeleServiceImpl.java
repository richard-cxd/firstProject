package pw.wechatbrother.base.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.mapper.TeleMapper;
import pw.wechatbrother.base.service.TeleService;
import pw.wechatbrother.base.utils.exception.YnTransactionalException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YN on 2018/3/22.
 */

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class TeleServiceImpl implements TeleService {
    @Autowired
    private TeleMapper teleMapper;

    @Override
    //控制开关灯
    public DetailDTO open(String controlId) {
        DetailDTO returnDetailDTO = new DetailDTO(true);
        try{
            String[] ids = controlId.split(",");
            for (String id : ids){
                LinkedHashMap<String,String> map = new LinkedHashMap();
                map.put("id_KZDLine",id);
                map.put("type","1");
                List<LinkedHashMap> listMap = teleMapper.open(map);
                System.out.println(listMap);
            }


        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    @Override
    public DetailDTO off(String controlId) {
        DetailDTO returnDetailDTO = new DetailDTO(true);
        try {
            String[] ids = controlId.split(",");
            for (String id : ids){

                    Map map = new LinkedHashMap();
                    map.put("id_KZDLine",id);
                    map.put("type","0");
                List<LinkedHashMap> listMap = teleMapper.off(map);
                System.out.println(listMap);

                }


        } catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    @Override
    public DetailDTO findEnterprise(String zoneid) {
        DetailDTO detailDTO = new DetailDTO(true);
        List<LinkedHashMap> listMap = teleMapper.findEnterprise(zoneid);
        detailDTO.setDetail(listMap);
        return detailDTO;
    }

    @Override
    public DetailDTO enterpriseDetail(String enterpriseId) {
        DetailDTO detailDTO = new DetailDTO(true);
        List<LinkedHashMap> listMap = teleMapper.enterpriseDetail(enterpriseId);
        detailDTO.setDetail(listMap);
        return detailDTO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.REPEATABLE_READ)
    public DetailDTO getControlStatus(String zoneid) {
        DetailDTO detailDTO = new DetailDTO(true);
        List<LinkedHashMap> listMap = teleMapper.getControlStatus(zoneid);
        detailDTO.setDetail(listMap);
        return detailDTO;
    }

    //开关挂牌
    @Override
    public DetailDTO hangOut(String controlId) {
        DetailDTO returnDetailDTO = new DetailDTO(true);
        String[] ids = controlId.split(",");
        for(String id : ids){
            List<LinkedHashMap> listMap = teleMapper.hangOut(id);
            String c = (String) listMap.get(0).get("c");
            if("0".equals(c)){
                returnDetailDTO.setStatus(false);
                returnDetailDTO.setErrorCode("-1");
                returnDetailDTO.setErrorMessage("线路【',"+controlId+",'】挂牌失败：线路已挂牌或正在运行中，必须先停电才能挂牌。");
                return returnDetailDTO;
            }
        }
        return returnDetailDTO;
    }

    //开关取消挂牌
    @Override
    public DetailDTO cancelHangOut(String controlId) {
        DetailDTO returnDetailDTO = new DetailDTO(true);
        String[] ids = controlId.split(",");
        for(String id : ids){
            List<LinkedHashMap> listMap = teleMapper.cancelHangOut(id);
            String c = (String) listMap.get(0).get("c");
            if("0".equals(c)){
                returnDetailDTO.setStatus(false);
                returnDetailDTO.setErrorCode("-1");
                returnDetailDTO.setErrorMessage("线路【',"+controlId+",'】摘牌失败：线路未挂牌无法启动摘牌。");
                return returnDetailDTO;
            }
        }
        return returnDetailDTO;
    }

/*    @Override
    public void test() {
        teleMapper.test();
        //int i = 1/0;
        Map map = teleMapper.selectId();
        System.out.println(map.toString());
    }*/
}
