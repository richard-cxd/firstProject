package pw.wechatbrother.base.service;

import pw.wechatbrother.base.dto.DetailDTO;

/**
 * Created by YN on 2018/3/22.
 */
public interface TeleService {


    //开
    public DetailDTO open(String controlId);
    //关
    public DetailDTO off(String controlId);

    DetailDTO findEnterprise(String zoneid);

    DetailDTO enterpriseDetail(String enterpriseId);

    DetailDTO getControlStatus(String zoneid);

    DetailDTO hangOut(String controlId);

    DetailDTO cancelHangOut(String controlId);

//    void test();
}
