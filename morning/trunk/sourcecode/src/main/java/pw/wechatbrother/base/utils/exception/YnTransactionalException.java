package pw.wechatbrother.base.utils.exception;

/** 定义一个运行时异常，用于事物的回滚
 * Created by zhengjingli on 2016/11/6.
 */
public class YnTransactionalException extends RuntimeException {
    boolean state=false;
    String errorMsg="";

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public YnTransactionalException() {}

    public YnTransactionalException(String message) {
        super(message);
    }
    public YnTransactionalException(boolean state,String errorMsg) {
        this.setState(state);
        this.setErrorMsg(errorMsg);
    }
}