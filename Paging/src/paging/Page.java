package paging;

/**
 * Created by sarahaly on 12/1/16.
 */
public class Page{
    protected Integer pageNum;
    protected Integer processNum;
    protected int loadTime;
    protected int evictedTime;
    protected int residencyTime;

    public Page(Integer pageNum, Integer processNum){
        this.pageNum = pageNum;
        this.processNum = processNum;
        this.loadTime = 0;
        this.evictedTime = 0;
        this.residencyTime = 0;

    }

    @Override
    public boolean equals(Object p){
        Page page = (Page) p;
        //System.out.println(page.pageNum);
        if((this.processNum == page.processNum) && (this.pageNum == page.pageNum)) {
            return true;
        }
        return false;
    }

    public String toString(){
        return "page: " + pageNum + " of process: " + processNum;
    }
}
