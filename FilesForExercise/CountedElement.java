
// Student ID: 3118197
// Name: WEI-TSUNG CHENG

public class CountedElement<E extends Comparable<E>> implements Comparable<CountedElement<E>> {
	private E element;
	private int count;

	public CountedElement(E e, int count){
        this.element = e;
        this.count = count;
	}
	
	public CountedElement(E e){
        this.element = e;
        this.count = 1;
	}

    public E getElement() {
        return element;
    }

    public void setElement(E element) {
        this.element = element;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String toString() {
        return element + " " + count;
    }
	
	public int compareTo(CountedElement<E> sC1) {
        return this.element.compareTo(sC1.element);
	}

}
