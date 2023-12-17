package me.basiqueevangelist.commonbridge.octo;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.transaction.Transaction;
import com.epherical.octoecon.api.user.User;
import org.jetbrains.annotations.Nullable;

public interface TransactionFactory {
    static TransactionFactory transaction(User user, double amount, Currency currency, Transaction.Type type) {
        return (response, message) ->
            new CommonTransaction(
                currency,
                response == Transaction.Response.SUCCESS ? amount : 0,
                user,
                message,
                response,
                type
            );
    }

    CommonTransaction finish(Transaction.Response response, @Nullable String message);

    default CommonTransaction success(@Nullable String message) {
        return finish(Transaction.Response.SUCCESS, message);
    }

    default CommonTransaction fail(@Nullable String message) {
        return finish(Transaction.Response.FAIL, message);
    }

    record CommonTransaction(
        Currency currency,
        double delta,
        User user,
        String message,
        Response response,
        Type type
    ) implements Transaction {
        @Override
        public Currency getCurrency() {
            return currency;
        }

        @Override
        public double getTransactionDelta() {
            return delta;
        }

        @Override
        public User getUser() {
            return user;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public Response getTransactionResponse() {
            return response;
        }

        @Override
        public void setTransactionResponse(Response response) {
            throw new UnsupportedOperationException("Cannot change transaction response");
        }

        @Override
        public Type getTransactionType() {
            return type;
        }
    }
}