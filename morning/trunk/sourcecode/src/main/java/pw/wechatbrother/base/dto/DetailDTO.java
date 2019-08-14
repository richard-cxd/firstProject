package pw.wechatbrother.base.dto;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class DetailDTO<T> extends RestStatus{
    public T detail;
    public String enumeration="0";//0表示无枚举，1表示为巡检已经结束,2表示需要预留积分提醒
    public DetailDTO(){
    }
    public DetailDTO(Boolean status){
        super(status);
    }
    public DetailDTO(Boolean status,T detail){
        super(status);
        this.detail=detail;
    }
    public T getDetail(){
        return detail;
    }
    public void setDetail(T detail){
        this.detail=detail;
    }

    public String getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(String enumeration) {
        this.enumeration = enumeration;
    }
}