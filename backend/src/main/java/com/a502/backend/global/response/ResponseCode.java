package com.a502.backend.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

	// Global
	API_SUCCESS_DOMAIN_METHOD("EXAMPLE001", "예상 메시지입니다."),

	//User
	API_SUCCESS_USER_CHECK_TELEPHONE("U001", "사용할 수 있는 번호 입니다."),
	API_SUCCESS_USER_CHECK_EMAIL("U002", "사용할 수 있는 이메일 입니다."),
	API_SUCCESS_LOGIN("U003", "로그인 되었습니다."),
	API_SUCCESS_SIGNUP("U004", "회원가입 되었습니다."),
	API_SUCCESS_KEYPAD_LIST("U005", "성공적으로 키패드가 반환되었습니다."),

	// account
	API_SUCCESS_ACCOUNT_EXIST("A001", "존재하는 계좌입니다."),
	API_SUCCESS_ACCOUNT_IS_SUFFICIENT("A002", "정상적으로 출금이 가능합니다."),
	API_SUCCESS_ACCOUNT_CREATE("AT003", "계좌생성 완료했습니다."),
	API_SUCCESS_GET_KEYPAD("AT004", "키패드를 성공적으로 전송했습니다."),
	API_FAIL_ACCOUNT_PASSWORD("AT005", "계좌 비밀번호가 불일치합니다."),
	API_SUCCESS_ACCOUNT_PASSWORD("AT006", "계좌 비밀번호가 일치합니다."),

	// allowance
	API_SUCCESS_ALLOWANCE_GET_BY_MONTH("AW001", "가계부 정보(1달) 조회에 성공하였습니다."),

	// Stock
	//receipt
	API_SUCCESS_CONVERT_IMAGE("R001","영수증 분석을 완료햐였습니다."),

   	// Stock
	API_SUCCESS_STOCK_BUY("S001", "성공적으로 매수 주문했습니다."),
	API_SUCCESS_STOCK_SELL("S002", "성공적으로 매도 주문했습니다."),
	API_SUCCESS_STOCK_PRICE_HISTORY_BAR("S003", "기간별 주가 조회(봉 그래프)에 성공하였습니다."),
	API_SUCCESS_STOCK_PRICE_HISTORY_LINE("S004", "기간별 주가 조회(선 그래프)에 성공하였습니다."),
	API_SUCCESS_STOCK_MINE("S005", "보유 주식 정보 조회에 성공했습니다."),
	API_SUCCESS_STOCK_GET_ALL_INFO("S006", "전체 주식 정보 조회에 성공했습니다."),
	API_SUCCESS_STOCK_GET_WAITING_INFO("S007", "미체결 주식 정보 조회에 성공했습니다."),
	API_SUCCESS_RANKING_USER("S008", "회원 랭킹조회에 성공했습니다."),
	API_SUCCESS_RANKING_LIST("S009", "1 ~ 10위 랭킹 조회에 성공했습니다."),
	API_SUCCESS_STOCK_GET_ONE_INFO("S010", "주식 정보 조회에 성공했습니다."),

	//Pay
	API_SUCCESS_PAY_TRANSFER("P001", "송금에 성공하였습니다."),

	// Loan
	API_SUCCESS_LOAN_APPLY("L001", "대출 신청에 성공하였습니다."),
	API_SUCCESS_LOAN_GET_ALL_FOR_CHILD("L002","전체 대출 내역 조회(아이기준)에 성공하였습니다."),
	API_SUCCESS_LOAN_GET_DETAIL_FOR_CHILD("L003","대출 상세 내역 조회(아이기준)에 성공하였습니다."),
	API_SUCCESS_LOAN_REPAY("L004", "정상적으로 상환되었습니다."),
	API_SUCCESS_LOAN_GET_ALL_FOR_PARENTS("L005", "전체 대출 내역 조회(부모기준)에 성공하였습니다."),
	API_SUCCESS_LOAN_GET_REQUESTED_FOR_PARENTS("L006", "대출 요청 내역 조회(부모기준)에 성공하였습니다."),
	API_SUCCESS_LOAN_ACCEPT_REQUEST("L007","대출 승인에 성공하였습니다."),
	API_SUCCESS_LOAN_REFUSE_REQUEST("L008","대출 거절에 성공하였습니다."),

	// ParkingDetail
	API_SUCCESS_PARKING_DETAILS_GET("PD001", "성공적으로 파킹통장 내역을 조회하였습니다."),
	API_SUCCESS_PARKING_GET_INFO("PD002", "성공적으로 파킹통장 정보를 조회하였습니다.");
	private final String code;
	private final String message;
}
