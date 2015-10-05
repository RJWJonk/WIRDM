package Main;

import java.util.Comparator;
import java.util.Map;

import Model.Word;

class ValueComparator implements Comparator<Object> {
    Map<String, Word> wordMap;
    public ValueComparator(Map<String, Word> base) {
        this.wordMap = base;
    }


	@Override
	public int compare(Object o1, Object o2) {
		wordMap.get(o1).equals((String)o1);
		if (wordMap.get(o1).getGetFrequency() >= wordMap.get(o2).getGetFrequency()) {
            return -1; 
        } else {
            return 1;
        }
	}

}
