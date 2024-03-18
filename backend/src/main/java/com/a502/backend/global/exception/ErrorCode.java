package com.a502.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Global
    API_ERROR_INTERNAL_SERVER(500, "G001", "서버 오류"),
    API_ERROR_INPUT_INVALID_VALUE(409, "G002", "잘못된 입력"),
    API_ERROR_NO_AUTHORIZATION(403, "G002", "권한 없음"),

    // User
    API_ERROR_USER_NOT_EXIST(400, "U001", "존재하지 않는 회원입니다."),

    // Account
    API_ERROR_ACCOUNT_NOT_EXIST(400, "A001", "존재하지 않는 계좌 입니다."),

    // AccountDetil
    API_ERROR_ACCOUNT_DETAIL_NOT_EXIST(400, "AD01", "존재하지 않는 계좌 거래 내역 입니다."),

    // Alert
    API_ERROR_ALERT_NOT_EXIST(400, "AL01", "존재하지 않는 알림 입니다."),

    // CashDetail
    API_ERROR_CASHDETAIL_NOT_EXIST(400, "CD01", "존재하지 않는 현금 거래내역 입니다."),

    // Memo
    API_ERROR_MEMO_NOT_EXIST(400, "M001", "존재하지 않는 메모입니다."),

    // Receipt
    API_ERROR_RECEIPT_NOT_EXIST(400, "R001", "존재하지 않는 영수증 입니다."),

    // ReceiptDetail
    API_ERROR_RECEIPT_DETAIL_NOT_EXIST(400, "RD01", "존재하지 않는 영수증 내역입니다."),

    // Stock
    API_ERROR_STOCK_NOT_EXIST(400, "S001", "존재하지 않는 주식입니다."),

    // StockDetail
    API_ERROR_STOCK_DETAIL_NOT_EXIST(400, "SD01", "존재하지 않는 주식정보입니다."),

    // StockHolding
    API_ERROR_STOCK_HOLDING_NOT_EXIST(400, "SH01", "존재하지 않는 거래입니다."),

    // StockSell
    API_ERROR_STOCKSELL_NOT_EXIST(400, "SS01", "존재하지 않는 주식매도 요청입니다."),

    // StockBuy
    API_ERROR_STOCKBUY_NOT_EXIST(400, "SB01", "존재하지 않는 주식매수 요청입니다."),

    // Parking
    API_ERROR_PARKING_NOT_EXIST(400, "P001", "존재하지 않는 파킹통장 입니다."),
    API_ERROR_PARKING_NOT_ENOUGH_BALANCE(400, "P002", "파킹통장에 잔액이 부족하여 주문할 수 없습니다."),

    // ParkingDetail
    API_ERROR_PARKING_DETAIL_NOT_EXIST(400, "PD01", "존재하지 않는 파킹통장 거래내역 입니다."),

    // Loan
    API_ERROR_LOAN_NOT_EXIST(400, "L001", "존재하지 않는 대출 상품입니다."),

    // LoanDetail
    API_ERROR_LOAN_DETAIL_NOT_EXIST(400, "LD01", "존재하지 않는 대출 납부 내역 입니다."),

    // LoanConversation
    API_ERROR_LOAN_CONVERSATION_NOT_EXIST(400, "LC01", "존재하지 않는 대출 대화입니다."),

    // LoanRefusal
    API_ERROR_LOAN_REFUSAL_NOT_EXIST(400, "LR01", "존재하지 않는 대출 거절입니다."),

    // Saving
    API_ERROR_SAVING_NOT_EXIST(400, "SV01", "존재하지 않는 적금 상품입니다."),

    ;
    private final int status;
    private final String code;
    private final String message;
}
