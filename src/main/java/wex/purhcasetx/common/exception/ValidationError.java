package wex.purhcasetx.common.exception;

public record ValidationError(String field, String violation) {
}
