package concrete;

import interfaces.IMessage;
import interfaces.IMessagePool;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class MessagePool implements IMessagePool, Serializable {
    private Set<IMessage> pool;

    public MessagePool() {
        this.pool = new HashSet<>();
    }

    @Override
    public void insertMessage(IMessage message) {
        this.pool.add(message);
    }

    @Override
    public boolean isMessageExists(IMessage message) {
        return this.pool.contains(message);
    }

    @Override
    public void clean() {
        this.pool.clear();
    }

    @Override
    public int getPoolSize() {
        return this.pool.size();
    }

    @Override
    public void setPool(Set<IMessage> pool) {
        this.pool = pool;
    }

    @Override
    public Set<IMessage> getPool() {
        return this.pool;
    }
}
