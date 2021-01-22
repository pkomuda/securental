package pl.lodz.p.it.securental.utils;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class PagingHelper {

    private final int page;
    private final int size;
    private String property;
    private String order;

    public Pageable withSorting() {
        if (StringUtils.isNullOrEmpty(property) || StringUtils.isNullOrEmpty(order)) {
            return withoutSorting();
        } else {
            if (order.equals("desc")) {
                return PageRequest.of(page, size, Sort.by(property).descending());
            } else {
                return PageRequest.of(page, size, Sort.by(property).ascending());
            }
        }
    }

    public Pageable withoutSorting() {
        return PageRequest.of(page, size);
    }
}
