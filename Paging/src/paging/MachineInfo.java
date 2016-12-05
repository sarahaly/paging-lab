package paging;

/**
 * Created by sarahaly on 11/25/16.
 */
public class MachineInfo {
    protected Integer machineSize;
    protected Integer pageSize;
    protected Integer processSize;
    protected Integer jobMix;
    protected Integer numReferences;
    protected String algo;

    public  MachineInfo(Integer machineSize, Integer pageSize, Integer processSize, Integer jobMix, Integer numReferences, String algo){
        this.machineSize = machineSize;
        this.pageSize = pageSize;
        this.processSize = processSize;
        this.jobMix = jobMix;
        this.numReferences = numReferences;
        this.algo = algo;
    }

    public String toString(){
        String str = "The machine size is " + machineSize + ".\n" +
                "The page size is " + pageSize + ".\n" +
                "The process size is " + processSize + ".\n" +
                "The job mix number is " + jobMix + ".\n" +
                "The number of references per process is " + numReferences + ".\n" +
                "The replacement algorithm is " + algo + ".\n";

        return str;

    }
}
