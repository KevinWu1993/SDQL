package sdql.fsyt.sdql.timeline;

import java.util.Comparator;

/**
 * Created by KevinWu on 2015/12/22.
 * 这个类用来定义排序方法，对时间进行比较
 */
public class DateComparator implements Comparator<DateText> {

	@Override
	public int compare(DateText rhs, DateText lhs) {
		return rhs.getTime().compareTo(lhs.getTime());
	}

}
