package rideflow.finance;

/** Simulated wallet balance for demonstrating payment polymorphism. */
public final class WalletPayment implements PaymentMethod {
    private double balance;
    public WalletPayment(double openingBalance) {
        if (!Double.isFinite(openingBalance) || openingBalance < 0) throw new IllegalArgumentException("Invalid balance");
        this.balance=openingBalance;
    }
    @Override public String name() { return "WALLET"; }
    @Override public PaymentResult charge(double amount) {
        if (amount > balance) return new PaymentResult(false,"Insufficient wallet funds");
        balance -= amount;
        return new PaymentResult(true,"Wallet charged (simulated)");
    }
    public double getBalance() { return balance; }
}
