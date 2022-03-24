package stockhelper.main;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class DivergeAccountsException extends RuntimeException {
    private Set<String> accounts;
}
