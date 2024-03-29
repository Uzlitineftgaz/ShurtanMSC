package uz.neft.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uz.neft.dto.*;
import uz.neft.dto.action.CollectionPointActionDto;
import uz.neft.dto.action.MiningSystemActionDto;
import uz.neft.dto.action.UppgActionDto;
import uz.neft.dto.action.WellActionDto;
import uz.neft.dto.constantValue.ConstantValueDto;
import uz.neft.entity.*;
import uz.neft.entity.action.CollectionPointAction;
import uz.neft.entity.action.MiningSystemAction;
import uz.neft.entity.action.UppgAction;
import uz.neft.entity.action.WellAction;
import uz.neft.entity.variables.Constant;
import uz.neft.entity.variables.ConstantValue;
import uz.neft.entity.variables.GasComposition;
import uz.neft.entity.variables.MiningSystemGasComposition;
import uz.neft.payload.ApiResponse;
import uz.neft.payload.ApiResponseObject;
import uz.neft.payload.ApiResponseObjectByPageable;
import uz.neft.repository.RoleRepository;
import uz.neft.repository.UserRepository;

import java.io.Serializable;

@Component
public class Converter {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * For responses
     **/
    public ApiResponse api(String message, boolean success) {
        return new ApiResponse(message, success);
    }

    public ApiResponse api(String message, boolean success, Object object) {
        return new ApiResponseObject(message, success, object);
    }

    public ApiResponse api(String message, boolean success, Object object, long totalElements, Integer totalPages) {
        return new ApiResponseObjectByPageable(message, success, object, totalElements, totalPages);
    }
    public ApiResponse api(String message, boolean success, Object object, long totalElements, Integer totalPages, int pageNumber) {
        return new ApiResponseObjectByPageable(message, success, object, totalElements, totalPages, pageNumber);
    }

    public ApiResponse apiError() {
        return api("Error", false);
    }

    public ApiResponse apiError(String message) {
        return api(message, false);
    }

    public ApiResponse apiError(Object object) {
        return api("Error", false, object);
    }

    public ApiResponse apiError(String message, Object object) {
        return api(message, false, object);
    }

    public ApiResponse apiSuccess() {
        return api("Success", true);
    }

    public ApiResponse apiSuccess(String message) {
        return api(message, true);
    }

    public ApiResponse apiSuccess(Object object) {
        return api("Success", true, object);
    }

    public ApiResponse apiSuccess(String message, Object object) {
        return api(message, true, object);
    }

    public ApiResponse apiSuccess(Object object, long totalElements, Integer totalPages) {
        return api("Success", true, object, totalElements, totalPages);
    }

    public ApiResponse apiSuccess(String message, Object object, long totalElements, Integer totalPages) {
        return api(message, true, object, totalElements, totalPages);
    }

    public ApiResponse apiSuccessObject(Object object) {
        return api("Success", true, object);
    }

    public ApiResponse apiSuccessObject(Object object, long totalElements, Integer totalPages) {
        return api("Success", true, object, totalElements, totalPages);
    }
    public ApiResponse apiSuccessObject(Object object, long totalElements, Integer totalPages, int number) {
        return api("Success", true, object, totalElements, totalPages, number);
    }



    /**
     * for response with status
     **/

    protected ResponseEntity<?> helpError(HttpStatus status) {
        return ResponseEntity.status(status).body(apiError(status.name()));
    }

    protected ResponseEntity<?> helpError(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(apiError(message));
    }

    protected ResponseEntity<?> helpError(HttpStatus status, Object object) {
        return ResponseEntity.status(status).body(apiError(status.name(), object));
    }

    protected ResponseEntity<?> helpError(HttpStatus status, String message, Object object) {
        return ResponseEntity.status(status).body(apiError(message, object));
    }

    protected ResponseEntity<?> helpSuccess(HttpStatus status) {
        return ResponseEntity.status(status).body(apiSuccess(status.name()));
    }

    protected ResponseEntity<?> helpSuccess(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(apiSuccess(message));
    }

    protected ResponseEntity<?> helpSuccess(HttpStatus status, Object object) {
        return ResponseEntity.status(status).body(apiSuccess(status.name(), object));
    }

    protected ResponseEntity<?> helpSuccess(HttpStatus status, String message, Object object) {
        return ResponseEntity.status(status).body(apiSuccess(message, object));
    }

    protected ResponseEntity<?> helpSuccess(HttpStatus status, Object object, long totalElements, Integer totalPages) {
        return ResponseEntity.status(status).body(apiSuccessObject(object, totalElements, totalPages));
    }
    protected ResponseEntity<?> helpSuccess(HttpStatus status, Object object, long totalElements, Integer totalPages, int number) {
        return ResponseEntity.status(status).body(apiSuccessObject(object, totalElements, totalPages,number));
    }

    /**
     * Error
     **/

    // Status 400
    public ResponseEntity<?> apiError400() {
        return helpError(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> apiError400(Object object) {
        return helpError(HttpStatus.BAD_REQUEST, object);
    }

    public ResponseEntity<?> apiError400(String message) {
        return helpError(HttpStatus.BAD_REQUEST, message);
    }

    public ResponseEntity<?> apiError400(String message, Object object) {
        return helpError(HttpStatus.BAD_REQUEST, message, object);
    }


    // Status 403
    public ResponseEntity<?> apiError403() {
        return helpError(HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<?> apiError403(Object object) {
        return helpError(HttpStatus.FORBIDDEN, object);
    }

    public ResponseEntity<?> apiError403(String message) {
        return helpError(HttpStatus.FORBIDDEN, message);
    }

    public ResponseEntity<?> apiError403(String message, Object object) {
        return helpError(HttpStatus.FORBIDDEN, message, object);
    }


    // Status 404
    public ResponseEntity<?> apiError404() {
        return helpError(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> apiError404(Object object) {
        return helpError(HttpStatus.NOT_FOUND, object);
    }

    public ResponseEntity<?> apiError404(String message) {
        return helpError(HttpStatus.NOT_FOUND, message);
    }

    public ResponseEntity<?> apiError404(String message, Object object) {
        return helpError(HttpStatus.NOT_FOUND, message, object);
    }

    // Status 409
    public ResponseEntity<?> apiError409() {
        return helpError(HttpStatus.CONFLICT);
    }

    public ResponseEntity<?> apiError409(Object object) {
        return helpError(HttpStatus.CONFLICT, object);
    }

    public ResponseEntity<?> apiError409(String message) {
        return helpError(HttpStatus.CONFLICT, message);
    }

    public ResponseEntity<?> apiError409(String message, Object object) {
        return helpError(HttpStatus.CONFLICT, message, object);
    }

    // Status 500
    public ResponseEntity<?> apiError500() {
        return helpError(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<?> apiError500(Object object) {
        return helpError(HttpStatus.INTERNAL_SERVER_ERROR, object);
    }

    public ResponseEntity<?> apiError500(String message) {
        return helpError(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public ResponseEntity<?> apiError500(String message, Object object) {
        return helpError(HttpStatus.INTERNAL_SERVER_ERROR, message, object);
    }

    /**
     * Success
     **/

    // Status 200
    public ResponseEntity<?> apiSuccess200() {
        return helpSuccess(HttpStatus.OK);
    }

    public ResponseEntity<?> apiSuccess200(Object object) {
        return helpSuccess(HttpStatus.OK, object);
    }

    public ResponseEntity<?> apiSuccess200(String message) {
        return helpSuccess(HttpStatus.OK, message);
    }

    public ResponseEntity<?> apiSuccess200(String message, Object object) {
        return helpSuccess(HttpStatus.OK, message, object);
    }

    public ResponseEntity<?> apiSuccess200(Object object, long totalElements, Integer totalPages) {
        return helpSuccess(HttpStatus.OK, object, totalElements, totalPages);
    }
    public HttpEntity<?> apiSuccess200(Object object, long totalElements, int totalPages, int number) {
        return helpSuccess(HttpStatus.OK, object, totalElements, totalPages,number);
    }

    // Status 201
    public ResponseEntity<?> apiSuccess201() {
        return helpSuccess(HttpStatus.CREATED);
    }

    public ResponseEntity<?> apiSuccess201(Object object) {
        return helpSuccess(HttpStatus.CREATED, object);
    }

    public ResponseEntity<?> apiSuccess201(String message) {
        return helpSuccess(HttpStatus.CREATED, message);
    }

    public ResponseEntity<?> apiSuccess201(String message, Object object) {
        return helpSuccess(HttpStatus.CREATED, message, object);
    }

    // Status 202
    public ResponseEntity<?> apiSuccess202() {
        return helpSuccess(HttpStatus.ACCEPTED);
    }

    public ResponseEntity<?> apiSuccess202(Object object) {
        return helpSuccess(HttpStatus.ACCEPTED, object);
    }

    public ResponseEntity<?> apiSuccess202(String message) {
        return helpSuccess(HttpStatus.ACCEPTED, message);
    }

    public ResponseEntity<?> apiSuccess202(String message, Object object) {
        return helpSuccess(HttpStatus.ACCEPTED, message, object);
    }

    /**
     * For data transfer objects (Dto)
     **/

    public UserDto userToUserDto(User user) {
        try {
            return UserDto
                    .builder()
                    .active(user.isActive())
                    .email(user.getEmail())
                    .fio(user.getFio())
                    .id(user.getId())
                    .phone(user.getPhone())
                    .username(user.getUsername())
                    .roleId(user.getRoles().stream().findFirst().get().getId())
                    .roleName(user.getRoles().stream().findFirst().get().getRoleName().name())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public MiningSystemDto miningSysToMiningSysDto(MiningSystem miningSystem) {
        try {
            return MiningSystemDto
                    .builder()
                    .Id(miningSystem.getId())
                    .name(miningSystem.getName())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UppgDto uppgToUppgDto(Uppg uppg) {
        try {
            return UppgDto
                    .builder()
                    .id(uppg.getId())
                    .name(uppg.getName())
                    .miningSystemId(uppg.getMiningSystem().getId())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CollectionPointDto collectionPointToCollectionPointDto(CollectionPoint collectionPoint) {
        try {
            return CollectionPointDto
                    .builder()
                    .id(collectionPoint.getId())
                    .name(collectionPoint.getName())
                    .uppgId(collectionPoint.getUppg().getId())
                    .active(collectionPoint.isActiveE())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public WellDto wellToWellDto(Well well) {
        try {
            return WellDto
                    .builder()
                    .id(well.getId())
                    .number(well.getNumber())
                    .horizon(well.getHorizon())
                    .altitude(well.getAltitude())
                    .commissioningDate(well.getCommissioningDate())
                    .depth(well.getDepth())
                    .drillingStartDate(well.getDrillingStartDate())
                    .x(well.getX())
                    .y(well.getY())
                    .c(well.getC())
                    .collectionPointId(well.getCollectionPoint().getId())
                    .category(well.getCategory())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public GasCompositionDto gasCompositionToGasCompositionDto(GasComposition composition) {
        try {
            return GasCompositionDto
                    .builder()
                    .id(composition.getId())
                    .name(composition.getName())
                    .criticalPressure(composition.getCriticalPressure())
                    .criticalTemperature(composition.getCriticalTemperature())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ConstantDto constantToConstantDto(Constant constant) {
        try {
            return ConstantDto
                    .builder()
                    .Id(constant.getId())
                    .name(constant.getName())
                    .description(constant.getDescription())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public MiningSystemGasCompositionDto miningSystemGasCompositionToMiningSystemGasCompositionDto(MiningSystemGasComposition miningSystemGasComposition) {
        try {
            return MiningSystemGasCompositionDto
                    .builder()
//                    .miningSystemName(miningSystemGasComposition.getMiningSystem().getName())
                    .id(miningSystemGasComposition.getId())
                    .miningSystemId(miningSystemGasComposition.getMiningSystem().getId())
                    .gasCompositionId(miningSystemGasComposition.getGasComposition().getId())
                    .gasCompositionName(miningSystemGasComposition.getGasComposition().getName())
                    .molarFraction(miningSystemGasComposition.getMolarFraction())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public WellActionDto wellActionToWellActionDto(WellAction wellAction) {
        try {
            if (wellAction==null) return null;
            return WellActionDto
                    .builder()
                    .actionId(wellAction.getId())
                    .wellId(wellAction.getWell().getId())
                    .pressure(wellAction.getPressure())
                    .temperature(wellAction.getTemperature())
                    .expend(wellAction.getExpend() / 1000)
                    .average_expend(wellAction.getAverage_expend())
                    .rpl(wellAction.getRpl())
                    .status(wellAction.getStatus())
                    .createdAt(wellAction.getCreatedAt())
                    .C(wellAction.getC())
                    .P_pkr(wellAction.getP_pkr())
                    .T_pkr(wellAction.getT_pkr())
                    .P_pr(wellAction.getP_pr())
                    .T_pr(wellAction.getT_pr())
                    .ro_air(wellAction.getRo_air())
                    .ro_gas(wellAction.getRo_gas())
                    .Ro_otn(wellAction.getRo_otn())
                    .Z(wellAction.getZ())
                    .delta(wellAction.getDelta())
                    .perforation_min(wellAction.getPerforation_min())
                    .perforation_max(wellAction.getPerforation_max())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CollectionPointActionDto collectionPointActionToCollectionPointActionDto(CollectionPointAction collectionPointAction) {
        if (collectionPointAction == null) return new CollectionPointActionDto();
        try {
            return CollectionPointActionDto
                    .builder()
                    .actionId(collectionPointAction.getId())
                    .expend(collectionPointAction.getExpend())
                    .pressure(collectionPointAction.getPressure())
                    .temperature(collectionPointAction.getTemperature())
                    .collectionPointId(collectionPointAction.getCollectionPoint().getId())
                    .createdAt(collectionPointAction.getCreatedAt())
                    .modified(collectionPointAction.getModified())
                    .door(collectionPointAction.isDoorOpen())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return new CollectionPointActionDto();
        }
    }

    public ConstantValueDto constantValueToConstValueDto(ConstantValue value) {
        System.out.println(value.getConstant());
        System.out.println(value.getConstant().getId());
        try {
            return ConstantValueDto
                    .builder()
                    .Id(value.getId())
                    .constantId(value.getConstant() == null ? null : value.getConstant().getId())
                    .miningSystemId(value.getMiningSystem() == null ? null : value.getMiningSystem().getId())
                    .uppgId(value.getUppg() == null ? null : value.getUppg().getId())
                    .collectionPointId(value.getCollectionPoint() == null ? null : value.getCollectionPoint().getId())
                    .wellId(value.getWell() == null ? null : value.getWell().getId())
                    .value(value.getValue())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public UppgActionDto uppgActionToUppgActionDto(UppgAction uppgAction) {
        try {
            if (uppgAction == null) return null;
            return UppgActionDto
                    .builder()
                    .actionId(uppgAction.getId())
                    .designedPerformance(uppgAction.getDesignedPerformance())
                    .actualPerformance(uppgAction.getActualPerformance())
                    .expend(uppgAction.getExpend())
                    .condensate(uppgAction.getCondensate())
                    .onWater(uppgAction.getOnWater())
                    .incomeTemperature(uppgAction.getIncomeTemperature())
                    .exitTemperature(uppgAction.getExitTemperature())
                    .incomePressure(uppgAction.getIncomePressure())
                    .exitPressure(uppgAction.getExitPressure())
                    .uppgId(uppgAction.getUppg().getId())
                    .createdAt(uppgAction.getCreatedAt())
                    .todayExpend(uppgAction.getTodayExpend())
                    .yesterdayExpend(uppgAction.getYesterdayExpend())
                    .thisMonthExpend(uppgAction.getThisMonthExpend())
                    .lastMonthExpend(uppgAction.getLastMonthExpend())
                    .modified(uppgAction.getModified())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public MiningSystemActionDto miningSystemActionToMiningSystemActionDto(MiningSystemAction miningSystemAction) {
        try {
            if (miningSystemAction == null) return null;
            return MiningSystemActionDto
                    .builder()
                    .actionId(miningSystemAction.getId())
                    .expend(miningSystemAction.getExpend())
                    .createdAt(miningSystemAction.getCreatedAt())
                    .lastMonthExpend(miningSystemAction.getLastMonthExpend())
                    .thisMonthExpend(miningSystemAction.getThisMonthExpend())
                    .lastYearExpend(miningSystemAction.getLastYearExpend())
                    .planThisMonth(miningSystemAction.getPlanThisMonth())
                    .planThisYear(miningSystemAction.getPlanThisYear())
                    .miningSystemId(miningSystemAction.getMiningSystem().getId())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Dto dto(Serializable serializable){

        if (serializable instanceof MiningSystemAction) return miningSystemActionToMiningSystemActionDto((MiningSystemAction) serializable);
        if (serializable instanceof UppgAction) return uppgActionToUppgActionDto((UppgAction) serializable);
        if (serializable instanceof ConstantValue) return constantValueToConstValueDto((ConstantValue) serializable);
        if (serializable instanceof CollectionPointAction) return collectionPointActionToCollectionPointActionDto((CollectionPointAction) serializable);
        if (serializable instanceof WellAction) return wellActionToWellActionDto((WellAction) serializable);
        if (serializable instanceof MiningSystemGasComposition) return miningSystemGasCompositionToMiningSystemGasCompositionDto((MiningSystemGasComposition) serializable);
        if (serializable instanceof Constant) return constantToConstantDto((Constant) serializable);
        if (serializable instanceof GasComposition) return gasCompositionToGasCompositionDto((GasComposition) serializable);
        if (serializable instanceof Well) return wellToWellDto((Well) serializable);
        if (serializable instanceof CollectionPoint) return collectionPointToCollectionPointDto((CollectionPoint) serializable);
        if (serializable instanceof Uppg) return uppgToUppgDto((Uppg) serializable);
        if (serializable instanceof MiningSystem) return miningSysToMiningSysDto((MiningSystem) serializable);
        if (serializable instanceof User) return userToUserDto((User) serializable);
        return null;

        //For preview
//        return switch (serializable){
//            case MiningSystemAction a -> miningSystemActionToMiningSystemActionDto(a);
//            case UppgAction a -> uppgActionToUppgActionDto(a);
//            case ConstantValue a -> constantValueToConstValueDto(a);
//            case CollectionPointAction a -> collectionPointActionToCollectionPointActionDto(a);
//            case WellAction a -> wellActionToWellActionDto(a);
//            case MiningSystemGasComposition a -> miningSystemGasCompositionToMiningSystemGasCompositionDto(a);
//            case Constant a -> constantToConstantDto(a);
//            case GasComposition a -> gasCompositionToGasCompositionDto(a);
//            case Well a -> wellToWellDto(a);
//            case CollectionPoint a -> collectionPointToCollectionPointDto(a);
//            case Uppg a -> uppgToUppgDto(a);
//            case MiningSystem a -> miningSysToMiningSysDto(a);
//            case User a -> userToUserDto(a);
//            default -> throw new IllegalStateException("Unexpected value: " + serializable);
//        };
    }

}

