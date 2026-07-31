package bo.capital.tec.pet.common.util;

public final class PaginationUtil {

    private PaginationUtil() {
    }

    public static int safePage(int page) {
        return Math.max(page, 0);
    }

    public static int safeSize(int size) {
        return Math.min(Math.max(size, 1), 100);
    }
}
