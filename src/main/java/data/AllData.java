package data;

import com.sun.org.apache.bcel.internal.generic.LUSHR;
import shape.IShape;

import java.io.Serializable;
import java.util.List;

/**
 * @author Yangzhe Xie
 * @date 21/9/19
 */
public class AllData implements Serializable {
    private List<IShape> shapeList;
    private List<ChatMessage> messageList;
    private List<String> userList;

//    public AllData(List<IShape> shapeList, List<ChatMessage> messageList) {
//        this.shapeList = shapeList;
//        this.messageList = messageList;
//    }


    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public List<IShape> getShapeList() {
        return shapeList;
    }

    public void setShapeList(List<IShape> shapeList) {
        this.shapeList = shapeList;
    }

    public List<ChatMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }
}
