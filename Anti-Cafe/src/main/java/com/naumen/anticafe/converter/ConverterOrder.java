package com.naumen.anticafe.converter;

import com.naumen.anticafe.DTO.receive.order.ShowDTO;
import com.naumen.anticafe.DTO.receive.order.ShowReserveDTO;
import com.naumen.anticafe.DTO.send.order.*;
import com.naumen.anticafe.domain.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConverterOrder {
    public static GuestOrderDTO convertToGuestOrderDTO(Guest g) {
        return new GuestOrderDTO(
                g.getCompositeId().getGuestId(),
                g.getName()
        );
    }
    public static List<GuestOrderDTO>  convertToListGuestOrderDTO(List<Guest> guestList) {
        List<GuestOrderDTO> guestsList = new ArrayList<>();
        for (Guest g : guestList) {
            guestsList.add(
                    ConverterOrder.convertToGuestOrderDTO(g)
            );
        }
        return guestsList;
    }

    public static GuestCartOrderDTO convertToGuestCartOrderDTO(GuestCart gc) {
        return new GuestCartOrderDTO(
                gc.getId(),
                gc.getProduct().getName(),
                gc.getQuantity(),
                gc.getGuest().getName()
        );
    }
    public static List<GuestCartOrderDTO> convertToListGuestCartOrderDTO(List<GuestCart> guestCartList) {
        List<GuestCartOrderDTO> guestCartDTOList = new ArrayList<>();
        for (GuestCart gc : guestCartList) {
            guestCartDTOList.add(ConverterOrder.convertToGuestCartOrderDTO(gc));
        }
        return guestCartDTOList;
    }

    public static ShowSendDTO convertToShowSendDTO(Order order,
                                                   List<GuestOrderDTO> guestsList,
                                                   List<GuestCartOrderDTO> guestCartList,
                                                   ShowDTO dto,
                                                   Employee employee,
                                                   boolean access) {
        Optional<String> gameZoneName;
        String reserveDate;
        String reserveTime;
        String endReserve;
        long gameZoneId;
        if (order.getGameZone() == null) {
            gameZoneName = Optional.empty();
            reserveDate = null;
            reserveTime = null;
            endReserve = null;
            gameZoneId = -1;
        } else {
            gameZoneName = Optional.of(order.getGameZone().getName());
            reserveDate = order.getReserveDate().format(DateTimeFormatter.ofPattern("dd MMMM, yyyy"));
            reserveTime = order.getReserveTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            endReserve = order.getEndReserve().format(DateTimeFormatter.ofPattern("HH:mm"));
            gameZoneId = order.getGameZone().getId();
        }
        return new ShowSendDTO(
                guestsList,
                guestCartList,
                Optional.ofNullable(dto.guestIdError()),
                Optional.ofNullable(dto.guestMessageError()),
                employee.getName(),
                order.getManager().getName(),
                gameZoneName,
                reserveDate,
                reserveTime,
                endReserve,
                order.getTaggedDelete(),
                order.getPayment(),
                order.getTotal(),
                order.getId(),
                gameZoneId,
                access
        );
    }

    public static GameZoneReserveDTO convertToGameZoneReserveDTO(GameZone gz) {
        return new GameZoneReserveDTO(gz.getId(), gz.getName());
    }
    public static List<GameZoneReserveDTO> convertToListGameZoneReserveDTO(List<GameZone> gameZoneList) {
        List<GameZoneReserveDTO> gameZoneDTOList = new ArrayList<>();
        for (GameZone gz : gameZoneList)
            gameZoneDTOList.add(
                    ConverterOrder.convertToGameZoneReserveDTO(gz)
            );
        return gameZoneDTOList;
    }

    public static ShowReserveSendDTO convertToShowReserveSendDTO(Employee employee,
                                                                 Long orderId,
                                                                 List<GameZoneReserveDTO> gameZoneList,
                                                                 List<String> dayOfReserve,
                                                                 int[][] freeTimesAndMaxHourReserve,
                                                                 ShowReserveDTO dto) {
        return new ShowReserveSendDTO(
                employee.getName(),
                orderId,
                gameZoneList,
                dayOfReserve,
                dto.gameZoneId(),
                dto.day(),
                Optional.ofNullable(freeTimesAndMaxHourReserve),
                Optional.ofNullable(dto.hourError()),
                dto.hourMessageError()
        );
    }

    public static LocalDate converterToLocalDate(String day) {
        //разделяем день и месяц
        String[] dayMonthYear = day.split("\\.");
        //создаем переменную даты и дня
        return LocalDate.of(
                Integer.parseInt("20" + dayMonthYear[2]),
                Integer.parseInt(dayMonthYear[1]),
                Integer.parseInt(dayMonthYear[0])
        );
    }
}
