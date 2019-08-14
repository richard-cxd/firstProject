package pw.wechatbrother.base.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.wechatbrother.base.dto.JsTree;
import pw.wechatbrother.base.mapper.JsTreeMapper;
import pw.wechatbrother.base.service.JsTreeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by YN on 2018/1/23.
 */
@Service
public class JsTreeServiceImpl implements JsTreeService {
    @Autowired
    JsTreeMapper jsTreeMapper;

    @Override
    public List<JsTree> getJsTree(String companyId) {
        List<JsTree> jsTrees = new ArrayList<JsTree>();
        List<Map> list = jsTreeMapper.findMonitoringAndElectrical(companyId);
        if (list.size() > 0) {
            for (Map map : list) {
                //监测点
                JsTree jsTree = new JsTree();
                String id = ((String) map.get("JCDAliasID")).trim() + ";" + String.valueOf(map.get("DFid")) + ";" + String.valueOf(map.get("SBTZId")) + ";" + String.valueOf(map.get("JCDType"));
                jsTree.setId(id);
                jsTree.setParent(String.valueOf(map.get("DFid")));
                jsTree.setText((String) map.get("JCDName"));
                jsTrees.add(jsTree);
            }
        }
        List<Map> list2 = jsTreeMapper.findElectricalAndEnterprise(companyId);
        if (list2.size() > 0) {
            for (Map map : list2) {
                //电房
                JsTree jsTree = new JsTree();
                jsTree.setId(String.valueOf(map.get("DFid")));
                jsTree.setParent(String.valueOf(map.get("YDQYid")));
                jsTree.setText((String) map.get("DFName"));
                jsTree.setIcon("/ynweb/assets/global/onLineMonitor/img/monitor/timg.png");
                jsTrees.add(jsTree);
            }
        }
        List<Map> list3 = jsTreeMapper.findEnterpriseNode(companyId);
        if (list3.size() > 0) {
            for (Map map : list3) {
                //用电企业
                JsTree jsTree = new JsTree();
                jsTree.setId(String.valueOf(map.get("YDQYid")));
                jsTree.setParent("#");
                jsTree.setText((String) map.get("YDQYName"));
                jsTree.setIcon("/ynweb/assets/global/onLineMonitor/img/monitor/dianimg.png");
                jsTrees.add(jsTree);
            }
        }
        return jsTrees;
    }

    @Override
    public List<JsTree> findJstreeByEnterprise(String enterpriseId) {
        List<JsTree> jsTrees = new ArrayList<JsTree>();
        List<Map> list = jsTreeMapper.findMonitoringAndElectricalByEnterprise(enterpriseId);
        if (list.size() > 0) {
            for (Map map : list) {
                //监测点
                JsTree jsTree = new JsTree();
                String id = ((String) map.get("JCDAliasID")).trim() + ";" + String.valueOf(map.get("DFid")) + ";" + String.valueOf(map.get("SBTZId")) + ";" + String.valueOf(map.get("JCDType"));
                jsTree.setId(id);
                jsTree.setParent(String.valueOf(map.get("DFid")));
                jsTree.setText((String) map.get("JCDName"));
                jsTrees.add(jsTree);
            }
        }
        List<Map> list2 = jsTreeMapper.findElectricalAndEnterpriseByEnterprise(enterpriseId);
        if (list2.size() > 0) {
            for (Map map : list2) {
                //电房
                JsTree jsTree = new JsTree();
                jsTree.setId(String.valueOf(map.get("DFid")));
                jsTree.setParent(String.valueOf(map.get("YDQYid")));
                jsTree.setText((String) map.get("DFName"));
                jsTree.setIcon("/ynweb/assets/global/onLineMonitor/img/monitor/timg.png");
                jsTrees.add(jsTree);
            }
        }
        List<Map> list3 = jsTreeMapper.findEnterpriseNodeByEnterprise(enterpriseId);
        if (list3.size() > 0) {
            for (Map map : list3) {
                //用电企业
                JsTree jsTree = new JsTree();
                jsTree.setId(String.valueOf(map.get("YDQYid")));
                jsTree.setParent("#");
                jsTree.setText((String) map.get("YDQYName"));
                jsTree.setIcon("/ynweb/assets/global/onLineMonitor/img/monitor/dianimg.png");
                jsTrees.add(jsTree);
            }
        }
        return jsTrees;
    }
}
