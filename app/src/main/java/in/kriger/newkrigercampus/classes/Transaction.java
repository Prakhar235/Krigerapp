package in.kriger.newkrigercampus.classes;

public class Transaction {

    String transaction_id,timestamp,bank_txnid;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getBank_txnid() {
        return bank_txnid;
    }

    public void setBank_txnid(String bank_txnid) {
        this.bank_txnid = bank_txnid;
    }
}
