package com.a502.backend.application.controller;

import com.a502.backend.application.entity.RankingDetail;
import com.a502.backend.application.facade.StockFacade;
import com.a502.backend.domain.stock.request.*;
import com.a502.backend.domain.stock.response.*;
import com.a502.backend.global.response.ApiResponse;
import com.a502.backend.global.response.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StockControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StockFacade stockFacade;

    @Mock
    private SimpMessageSendingOperations sendingOperations;

    @InjectMocks
    private StockController stockController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(stockController).build();
    }

    @Test
    void testStockBuy() throws Exception {
        PriceAndStockOrderList mockResult = PriceAndStockOrderList.builder()
                .price(1000)
                .stockOrderList(Collections.emptyList())
                .build();

        when(stockFacade.getStockOrderInfo(anyString())).thenReturn(mockResult);
        doNothing().when(stockFacade).stockBuy(any(StockTransactionRequest.class));

        mockMvc.perform(post("/api/stock/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Stock A\", \"price\": 100, \"cnt_total\": 10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_STOCK_BUY.getMessage()));

        verify(stockFacade, times(1)).stockBuy(any(StockTransactionRequest.class));
    }

    @Test
    void testStockSell() throws Exception {
        PriceAndStockOrderList mockResult = PriceAndStockOrderList.builder()
                .price(1000)
                .stockOrderList(Collections.emptyList())
                .build();

        when(stockFacade.getStockOrderInfo(anyString())).thenReturn(mockResult);
        doNothing().when(stockFacade).stockSell(any(StockTransactionRequest.class));

        mockMvc.perform(post("/api/stock/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Stock A\", \"price\": 100, \"cnt_total\": 5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_STOCK_SELL.getMessage()));

        verify(stockFacade, times(1)).stockSell(any(StockTransactionRequest.class));
    }

    @Test
    void testGetStockGraphInfosByLine() throws Exception {
        List<StockPriceHistoryByLine> response = Collections.singletonList(
                StockPriceHistoryByLine.builder().price(100).date(LocalDate.now()).build()
        );
        when(stockFacade.getStockGraphInfosByLine(anyString(), anyInt())).thenReturn(response);

        mockMvc.perform(post("/api/stock/price/history/line")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Stock A\", \"period\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_STOCK_PRICE_HISTORY_LINE.getMessage()))
                .andExpect(jsonPath("$.data[0].price").value(100));

        verify(stockFacade, times(1)).getStockGraphInfosByLine(anyString(), anyInt());
    }

    @Test
    void testGetStockGraphInfosByBar() throws Exception {
        List<StockPriceHistoryByBar> response = Collections.singletonList(
                StockPriceHistoryByBar.builder().x(1627849200000L).y(List.of(100, 120, 90, 110)).build()
        );
        when(stockFacade.getStockGraphInfosByBar(anyString(), anyInt())).thenReturn(response);

        mockMvc.perform(post("/api/stock/price/history/bar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Stock A\", \"period\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_STOCK_PRICE_HISTORY_BAR.getMessage()))
                .andExpect(jsonPath("$.data[0].x").value(1627849200000L))
                .andExpect(jsonPath("$.data[0].y[0]").value(100))
                .andExpect(jsonPath("$.data[0].y[1]").value(120));

        verify(stockFacade, times(1)).getStockGraphInfosByBar(anyString(), anyInt());
    }

    @Test
    void testGetAllStock() throws Exception {
        List<TotalStockList> stockList = Collections.singletonList(
                TotalStockList.builder()
                        .name("Stock A")
                        .price(1000)
                        .incomeRatio(1.5)
                        .transCnt(100)
                        .imageUrl("http://example.com/image.png")
                        .build()
        );

        TotalStockListResponse response = TotalStockListResponse.builder()
                .stock(stockList)
                .build();
        when(stockFacade.getTotalStockList()).thenReturn(response);

        mockMvc.perform(post("/api/stock/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_STOCK_GET_ALL_INFO.getMessage()))
                .andExpect(jsonPath("$.data.stock[0].name").value("Stock A"));

        verify(stockFacade, times(1)).getTotalStockList();
    }

    @Test
    void testGetMyStocks() throws Exception {
        MyStockListResponse response = MyStockListResponse.builder()
                .myStockList(Collections.emptyList())
                .totalIncome(1000)
                .totalPrice(10000)
                .build();

        when(stockFacade.getMyStockList()).thenReturn(response);

        mockMvc.perform(post("/api/stock/mine")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_STOCK_MINE.getMessage()))
                .andExpect(jsonPath("$.data.totalIncome").value(1000))
                .andExpect(jsonPath("$.data.totalPrice").value(10000));

        verify(stockFacade, times(1)).getMyStockList();
    }

    @Test
    void testGetMyWaitingStockOrders() throws Exception {
        MyWaitingStockOrderResponse response = MyWaitingStockOrderResponse.builder()
                .transaction(Collections.emptyList())
                .build();

        when(stockFacade.getMyWaitingStockOrder()).thenReturn(response);

        mockMvc.perform(post("/api/stock/order/wait")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_STOCK_GET_WAITING_INFO.getMessage()));

        verify(stockFacade, times(1)).getMyWaitingStockOrder();
    }

    @Test
    void testGetRanking() throws Exception {
        RankingDetail response = RankingDetail.builder()
                .childName("Child A")
                .balance(10000)
                .rank(1)
                .build();

        when(stockFacade.getRanking()).thenReturn(response);

        mockMvc.perform(get("/api/stock/ranking/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_RANKING_USER.getMessage()))
                .andExpect(jsonPath("$.data.childName").value("Child A"))
                .andExpect(jsonPath("$.data.balance").value(10000))
                .andExpect(jsonPath("$.data.rank").value(1));

        verify(stockFacade, times(1)).getRanking();
    }

    @Test
    void testGetRankingList() throws Exception {
        RankingDetail rankingDetail = RankingDetail.builder()
                .childName("Child A")
                .balance(10000)
                .rank(1)
                .build();

        RankingResponse response = new RankingResponse(Collections.singletonList(rankingDetail));
        when(stockFacade.getRanknigList()).thenReturn(response);

        mockMvc.perform(get("/api/stock/ranking/total")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_RANKING_LIST.getMessage()))
                .andExpect(jsonPath("$.data.ranks[0].childName").value("Child A"));

        verify(stockFacade, times(1)).getRanknigList();
    }

}