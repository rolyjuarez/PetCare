package bo.capital.tec.pet.common.util;

public final class PaginationUtil {
    private PaginationUtil() {}

    public static int safePage(int page) {
        return Math.max(0, page);
    }

    public static int safeSize(int size) {
        return Math.min(Math.max(1, size), 100);
    }
}
