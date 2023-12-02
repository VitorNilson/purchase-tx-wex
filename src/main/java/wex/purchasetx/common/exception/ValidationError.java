package wex.purchasetx.common.exception;

public record ValidationError(String field, String violation) {
}
