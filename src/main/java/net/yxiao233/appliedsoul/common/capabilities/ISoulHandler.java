package net.yxiao233.appliedsoul.common.capabilities;

public interface ISoulHandler {
    enum Action{
        EXECUTE,
        SIMULATE;
        public boolean execute(){
            return this == EXECUTE;
        }

        public boolean simulate(){
            return this == SIMULATE;
        }
    }

    int getSoulTanks();
    int getSoulInTank(int tank);
    int getTankCapacity(int tank);
    int fill(int amount, Action action);
    int drain(int maxDrain, Action action);
}
