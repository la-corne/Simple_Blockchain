package interfaces;

import java.util.Set;

public interface IMessagePool {

    public void insertMessage(IMessage message);

    public boolean isMessageExists(IMessage message);

    public void clean();

    public int getPoolSize();

    public void setPool(Set<IMessage> pool);

    public Set<IMessage> getPool();
}
