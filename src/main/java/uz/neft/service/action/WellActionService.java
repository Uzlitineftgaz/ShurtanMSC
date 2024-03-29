package uz.neft.service.action;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.neft.dto.WellDto;
import uz.neft.dto.action.ObjectWithActionsDto;
import uz.neft.dto.action.WellActionDto;
import uz.neft.dto.special.WellActionLite;
import uz.neft.entity.*;
import uz.neft.entity.action.CollectionPointAction;
import uz.neft.entity.action.UppgAction;
import uz.neft.entity.action.WellAction;
import uz.neft.entity.enums.WellStatus;
import uz.neft.entity.variables.*;
import uz.neft.repository.*;
import uz.neft.repository.action.CollectionPointActionRepository;
import uz.neft.repository.action.UppgActionRepository;
import uz.neft.repository.action.WellActionRepository;
import uz.neft.repository.constants.ConstantRepository;
import uz.neft.repository.constants.MiningSystemConstantRepository;
import uz.neft.service.Calculator;
import uz.neft.utils.Converter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WellActionService {
    private final WellActionRepository wellActionRepository;
    private final WellRepository wellRepository;
    private final Converter converter;
    private final MiningSystemGasCompositionRepository miningSystemMiningSystemGasCompositionRepository;
    private final MiningSystemConstantRepository miningSystemConstantRepository;
    private final ConstantRepository constantRepository;
    private final UserRepository userRepository;
    private final CollectionPointRepository collectionPointRepository;
    private final MiningSystemRepository miningSystemRepository;
    private final UppgRepository uppgRepository;
    private final UppgActionRepository uppgActionRepository;
    private final CollectionPointActionRepository collectionPointActionRepository;
    private final Logger logger;


    public WellActionService(WellActionRepository wellActionRepository, MiningSystemConstantRepository miningSystemConstantRepository, WellRepository wellRepository, Converter converter, MiningSystemRepository miningSystemRepository, GasCompositionRepository gasCompositionRepository, MiningSystemGasCompositionRepository miningSystemMiningSystemGasCompositionRepository, Calculator calculator, ConstantRepository constantRepository, UserRepository userRepository, CollectionPointRepository collectionPointRepository, UppgRepository uppgRepository, UppgActionRepository uppgActionRepository, CollectionPointActionRepository collectionPointActionRepository, Logger logger) {
        this.wellActionRepository = wellActionRepository;
        this.wellRepository = wellRepository;
        this.converter = converter;
        this.miningSystemMiningSystemGasCompositionRepository = miningSystemMiningSystemGasCompositionRepository;
        this.miningSystemConstantRepository = miningSystemConstantRepository;
        this.constantRepository = constantRepository;
        this.userRepository = userRepository;
        this.collectionPointRepository = collectionPointRepository;
        this.miningSystemRepository = miningSystemRepository;
        this.uppgRepository = uppgRepository;
        this.uppgActionRepository = uppgActionRepository;
        this.collectionPointActionRepository = collectionPointActionRepository;
        this.logger = logger;
    }


    public double expend(double temperature,double pressure, MiningSystem miningSystem){
        /**
         * List miningSystemGasCompositions
         **/
        List<MiningSystemGasComposition> miningSystemGasCompositions = miningSystemMiningSystemGasCompositionRepository.findAllByMiningSystem(miningSystem);

        /**
         * List molarFractions
         **/
        List<Double> molarFractions = miningSystemGasCompositions.stream().map(MiningSystemGasComposition::getMolarFraction).collect(Collectors.toList());

        /**
         * List criticalPressure
         **/
        List<Double> criticalPressure = miningSystemGasCompositions.stream().map(MiningSystemGasComposition::getGasComposition).map(GasComposition::getCriticalPressure).collect(Collectors.toList());

        /**
         * List criticalTemperature
         **/
        List<Double> criticalTemperature = miningSystemGasCompositions.stream().map(MiningSystemGasComposition::getGasComposition).map(GasComposition::getCriticalTemperature).collect(Collectors.toList());

        /**
         * Р_ПКР
         **/
        double P_pkr = Calculator.pseudoCriticalPressureOrTemperature(molarFractions, criticalPressure);

        /**
         * T_ПКР
         **/
        double T_pkr = Calculator.pseudoCriticalPressureOrTemperature(molarFractions, criticalTemperature);

        /**
         * Asosiy kiritiladigan bosim P_u
         **/
        double P_u = pressure;

        /**
         * Asosiy kiritiladigan temperatura T_u
         **/
        double T_u = temperature + 273;

        /**
         * P_pr
         **/
        double P_pr = Calculator.reducedPressure(P_u, P_pkr);

        /**
         * T_pr
         **/
        double T_pr = Calculator.reducedTemperature(T_u, T_pkr);

        /**
         * roGas   -   ρ_газа – плотность газа при стандартных условиях (standart
         * sharoitda gaz zichligi), kg/m3
         **/

        Constant constRoGas = constantRepository.findByName(ConstantNameEnums.RO_GAS);
        MiningSystemConstant roGas = miningSystemConstantRepository.findByMiningSystemAndConstant(miningSystem, constRoGas);

        /**
         * roAir  -  ρ_возд – плотность воздуха при стандартных условиях (standart
         * sharoitda havo zichligi), kg/m3
         **/
        Constant constRoAir = constantRepository.findByName(ConstantNameEnums.RO_AIR);
        MiningSystemConstant roAir = miningSystemConstantRepository.findByMiningSystemAndConstant(miningSystem, constRoAir);

        /**
         * Относительная плотность газа ( ρ_отн )
         **/
        double Ro_otn = Calculator.relativeDensity(roGas.getValue(), roAir.getValue());

        /**
         * Z - Коэффициент сверхсжимаемости газа
         **/
        double Z = Calculator.superCompressFactor(P_u, T_u, Ro_otn);

        /**
         * delta - Поправочный коэффициент, зависящий от приведенных
         * давления и температуры (δ)
         **/
        double delta = Calculator.correctionFactor_P_T(T_pr, P_pr);

        //MUAMMO
        double C = 88;

        /**
         * D_well - Средний дебит скважин месторождения Шуртан ( D_СКВ )
         **/
        double D_well = Calculator.averageProductionRate(C, P_u, delta, Ro_otn, Z, T_u);
        return D_well;
    }

    /**
     * Manually
     **/
    public ResponseEntity<?> addManually(User user, WellActionDto dto) {

        Optional<Well> well = wellRepository.findById(dto.getWellId());
//        if (user == null) return converter.apiError();
        if (!well.isPresent()) return converter.apiError404("Well not found!");
        Integer miningSystemId = well.get().getCollectionPoint().getUppg().getMiningSystem().getId();
        MiningSystem miningSystem = well.get().getCollectionPoint().getUppg().getMiningSystem();
        Optional<WellAction> oldAction = wellActionRepository.findFirstByWellOrderByCreatedAtDesc(well.get());
        /**
         * List miningSystemGasCompositions
         **/
        List<MiningSystemGasComposition> miningSystemGasCompositions = miningSystemMiningSystemGasCompositionRepository.findAllByMiningSystem(miningSystem);

        /**
         * List molarFractions
         **/
        List<Double> molarFractions = miningSystemGasCompositions.stream().map(MiningSystemGasComposition::getMolarFraction).collect(Collectors.toList());

        /**
         * List criticalPressure
         **/
        List<Double> criticalPressures = miningSystemGasCompositions.stream().map(MiningSystemGasComposition::getGasComposition).map(GasComposition::getCriticalPressure).collect(Collectors.toList());

        /**
         * List criticalTemperature
         **/
        List<Double> criticalTemperatures = miningSystemGasCompositions.stream().map(MiningSystemGasComposition::getGasComposition).map(GasComposition::getCriticalTemperature).collect(Collectors.toList());

        /**
         * Р_ПКР
         **/
        double P_pkr = Calculator.pseudoCriticalPressureOrTemperature(molarFractions, criticalPressures);

        /**
         * T_ПКР
         **/
        double T_pkr = Calculator.pseudoCriticalPressureOrTemperature(molarFractions, criticalTemperatures);

        /**
         * Asosiy kiritiladigan bosim P_u
         **/
        double P_u = dto.getPressure();

        /**
         * Asosiy kiritiladigan temperatura T_u
         **/
        double T_u = dto.getTemperature() + 273;

        /**
         * P_pr
         **/
        double P_pr = Calculator.reducedPressure(P_u, P_pkr);

        /**
         * T_pr
         **/
        double T_pr = Calculator.reducedPressure(T_u, T_pkr);

        /**
         * roGas   -   ρ_газа – плотность газа при стандартных условиях (standart
         * sharoitda gaz zichligi), kg/m3
         **/

        Constant constRoGas = constantRepository.findByName(ConstantNameEnums.RO_GAS);
        MiningSystemConstant roGas = miningSystemConstantRepository.findByMiningSystemAndConstant(miningSystem, constRoGas);

        /**
         * roAir  -  ρ_возд – плотность воздуха при стандартных условиях (standart
         * sharoitda havo zichligi), kg/m3
         **/
        Constant constRoAir = constantRepository.findByName(ConstantNameEnums.RO_AIR);
        MiningSystemConstant roAir = miningSystemConstantRepository.findByMiningSystemAndConstant(miningSystem, constRoAir);

        /**
         * Относительная плотность газа ( ρ_отн )
         **/
        double Ro_otn = Calculator.relativeDensity(roGas.getValue(), roAir.getValue());

        /**
         * Z - Коэффициент сверхсжимаемости газа
         **/
        double Z = Calculator.superCompressFactor(P_u, T_u, Ro_otn);

        /**
         * delta - Поправочный коэффициент, зависящий от приведенных
         * давления и температуры (δ)
         **/
        double delta = Calculator.correctionFactor_P_T(T_pr, P_pr);

        //MUAMMO
        double C = well.get().getC();
        if (C==0) C=88;

        /**
         * D_well - Средний дебит скважин месторождения Шуртан ( D_СКВ )
         **/
        double D_well = Calculator.averageProductionRate(C, P_u, delta, Ro_otn, Z, T_u);
//        System.out.println("D_well = "+D_well);

        try {
            WellAction wellAction = WellAction
                    .builder()
                    .user(userRepository.findById(1).get())
                    .temperature(dto.getTemperature())
                    .pressure(dto.getPressure())
                    .status(dto.getStatus())
                    .rpl(dto.getRpl())
                    .well(well.get())
                    .average_expend(D_well)
                    .expend(D_well)
                    .P_pkr(P_pkr)
                    .P_pr(P_pr)
                    .T_pkr(T_pkr)
                    .T_pr(T_pr)
                    .Z(Z)
                    .C(C)
                    .Ro_otn(Ro_otn)
                    .delta(delta)
                    .ro_gas(roGas.getValue())
                    .ro_air(roAir.getValue())
                    .build();
            WellAction save = wellActionRepository.save(wellAction);

            WellActionDto wellActionDto = (WellActionDto) converter.dto(save);
            execute(well.get().getCollectionPoint().getUppg());
//            System.out.println("DEBIT = "+sumAllExpendByUppg(well.get().getCollectionPoint().getUppg()));

            return converter.apiSuccess201(wellActionDto);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError404();
        }
    }


    //Алгоритм расчета прогнозных отборов газа и конденсата (для диаграммы)
    public double rpl(WellAction action){
        try {
            //        Optional<WellAction> action = wellActionRepository.findFirstByWellOrderByCreatedAtDesc(well);
            Constant constA = constantRepository.findByName(ConstantNameEnums.A);
            Constant constB = constantRepository.findByName(ConstantNameEnums.B);
            Constant constS = constantRepository.findByName(ConstantNameEnums.S);
            Constant constTeta = constantRepository.findByName(ConstantNameEnums.TETA);

            MiningSystemConstant A = miningSystemConstantRepository.findByMiningSystemAndConstant(action.getWell().getCollectionPoint().getUppg().getMiningSystem(), constA);
            MiningSystemConstant B = miningSystemConstantRepository.findByMiningSystemAndConstant(action.getWell().getCollectionPoint().getUppg().getMiningSystem(), constB);
            MiningSystemConstant S = miningSystemConstantRepository.findByMiningSystemAndConstant(action.getWell().getCollectionPoint().getUppg().getMiningSystem(), constS);
            MiningSystemConstant Teta = miningSystemConstantRepository.findByMiningSystemAndConstant(action.getWell().getCollectionPoint().getUppg().getMiningSystem(), constTeta);
            return Calculator.rplWell(action.getPressure(),S.getValue(),A.getValue(),B.getValue(),Teta.getValue(),action.getExpend());

        }catch (Exception e){
            return 0;
        }

    }

    public HttpEntity<?> getWells() {
        try {
            List<Well> all = wellRepository.findAll();

            List<WellDto> wellDtos = all.stream().map(well -> (WellDto) converter.dto(well)).collect(Collectors.toList());

            return converter.apiSuccess200(wellDtos);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409("Error in fetching all wells");
        }
    }

    public HttpEntity<?> getWellsWithAction() {
        try {
            List<Well> all = wellRepository.findAll();

            return wellActionDtos(all);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409("Error in fetching collection points with actions");
        }
    }

    public HttpEntity<?> getWellsWithActionByCollectionPoint(Integer id) {
        try {
            Optional<CollectionPoint> byId = collectionPointRepository.findById(id);
            if (byId.isEmpty()) return converter.apiError404("collection point not found");

            List<Well> wells = wellRepository.findAllByCollectionPointOrderByIdAsc(byId.get());

            return wellActionDtos(wells);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409("Error in fetching wells with actions by collection point ");
        }
    }

    public HttpEntity<?> getByCollectionPoint(Integer id) {
        try {
            Optional<CollectionPoint> byId = collectionPointRepository.findById(id);
            if (byId.isEmpty()) return converter.apiError404("collection point not found");

            List<Well> wells = wellRepository.findAllByCollectionPointOrderByIdAsc(byId.get());

            List<WellDto> collect = wells.stream().map(well -> (WellDto) converter.dto(well)).collect(Collectors.toList());

            return converter.apiSuccess200(collect);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409("Error in fetching wells by collection point ");
        }
    }

    public HttpEntity<?> getWell(Integer id) {
        try {
            Optional<Well> byId = wellRepository.findById(id);
            if (byId.isEmpty()) return converter.apiError404("well not found");
            WellDto dto = (WellDto) converter.dto(byId.get());
            return converter.apiSuccess200(dto);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409("Error in fetching well by id");
        }
    }

    public HttpEntity<?> getWellWithAction(Integer id) {
        try {
            Optional<Well> byId = wellRepository.findById(id);
            if (byId.isEmpty()) return converter.apiError404("well not found");

            Optional<WellAction> firstByWell = wellActionRepository.findFirstByWellOrderByCreatedAtDesc(byId.get());
//            if (!firstByWell.isPresent()) return converter.apiError404("well action not found");

            WellDto wellDto = (WellDto) converter.dto(byId.get());
            WellActionDto wellActionDto=new WellActionDto();
            if (firstByWell.isPresent()){
                wellActionDto = (WellActionDto) converter.dto(firstByWell.get());
            }

            ObjectWithActionsDto dto = ObjectWithActionsDto
                    .builder()
                    .objectDto(wellDto)
                    .objectActionDto(wellActionDto)
                    .build();

            return converter.apiSuccess200(dto);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409("Error in fetching collection point by id");
        }
    }

    // helper method
    public HttpEntity<?> wellActionDtos(List<Well> wells) {
        if (wells.isEmpty()) return converter.apiSuccess200("Empty list");

        List<ObjectWithActionsDto> collect = wells.stream().map(item -> {
            Optional<Well> byId1 = wellRepository.findById(item.getId());

            Optional<WellAction> firstByWell = wellActionRepository.findFirstByWellOrderByCreatedAtDesc(byId1.get());

            WellDto wellDto = (WellDto) converter.dto(byId1.get());
            WellActionDto wellActionDto=new WellActionDto();
            if (firstByWell.isPresent()){
                wellActionDto = (WellActionDto) converter.dto(firstByWell.get());
            }

            return ObjectWithActionsDto
                    .builder()
                    .objectDto(wellDto)
                    .objectActionDto(wellActionDto)
                    .build();
        }).collect(Collectors.toList());

        return converter.apiSuccess200(collect);
    }

    public HttpEntity<?> getAllByUppg(Integer id) {
        try {
            if (id == null) return converter.apiError400("id is null!");
            List<Well> wellList = wellRepository.findAllByUppgId(id);
            return converter.apiSuccess200(wellList.stream().map(well -> (WellDto) converter.dto(well)).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409();
        }
    }

    public HttpEntity<?> getAllWithActionsByUppg(Integer id) {
        try {
            if (id == null) return converter.apiError400("id is null!");
            List<Well> wellList = wellRepository.findAllByUppgId(id);
            List<ObjectWithActionsDto> list = new ArrayList<>();
            wellList
                    .forEach(
                            w ->
                                    list
                                            .add(new ObjectWithActionsDto(
                                                    converter.dto(w),
                                                    converter.dto(
                                                            wellActionRepository
                                                                    .findFirstByWellOrderByCreatedAtDesc(w).get()))));
            return converter.apiSuccess200(list);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409();
        }
    }


    public HttpEntity<?> getAllActionsByWell(Integer wellId, Optional<Integer> page, Optional<Integer> pageSize, Optional<String> sortBy) {
        try {
            if (wellId == null) return converter.apiError400("action id is null!");
            Optional<Well> well = wellRepository.findById(wellId);
            if (well.isEmpty()) return converter.apiError404("well not found");

            Pageable pg = PageRequest.of(page.orElse(0), pageSize.orElse(10), Sort.Direction.DESC, sortBy.orElse("createdAt"));

            Page<WellAction> wellActions = wellActionRepository.findAllByWellOrderByCreatedAtDesc(well.get(), pg);

            Stream<WellActionDto> wellActionDtoStream = wellActions.stream().map(wa -> (WellActionDto) converter.dto(wa));

            return converter.apiSuccess200(wellActionDtoStream, wellActions.getTotalElements(), wellActions.getTotalPages(), wellActions.getNumber());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409();
        }
    }


    public HttpEntity<?> getAllByMiningSystem(Integer id) {
        try {
            if (id == null) return converter.apiError400("id is null!");
            List<Well> wellList = wellRepository.findAllByMiningSystemId(id);
            return converter.apiSuccess200(wellList.stream().map(well -> (WellDto) converter.dto(well)).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409();
        }
    }


    public HttpEntity<?> getAllWithActionByMiningSystem(Integer id) {
        try {
            if (id == null) return converter.apiError400("id is null!");
            List<Well> wellList = wellRepository.findAllByMiningSystemId(id);
            List<ObjectWithActionsDto> list = new ArrayList<>();
            wellList
                    .forEach(
                            w ->
                                    list
                                            .add(new ObjectWithActionsDto(
                                                    converter.dto(w),
                                                    converter.dto(
                                                            Objects.requireNonNull(wellActionRepository
                                                                    .findFirstByWellOrderByCreatedAtDesc(w).orElse(null))))));
            return converter.apiSuccess200(list);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409();
        }
    }

    public List<ObjectWithActionsDto> getAllWithActionByMiningSystemWs(Integer id) {
        List<ObjectWithActionsDto> list = new ArrayList<>();
        try {
            List<Well> wellList = wellRepository.findAllByMiningSystemId(id);
            wellList
                    .forEach(
                            w ->
                                    list
                                            .add(new ObjectWithActionsDto(
                                                    converter.dto(w),
                                                    converter.dto(
                                                            Objects.requireNonNull(wellActionRepository
                                                                    .findFirstByWellOrderByCreatedAtDesc(w).orElse(null))))));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
        return list;
    }

    public ResponseEntity<?> getCountByInWork() {
        try {
            return converter.apiSuccess200();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409();
        }
    }

    public HttpEntity<?> getStatByStatus() {
        try{
            List<MiningSystem> miningSystemList = miningSystemRepository.findAllByOrderById();

//            Map<String, Integer> map=new HashMap<>();
            List<Map<String,Object>> stats=new ArrayList<>();
//            Map<String,Map<String,Object>> result=new HashMap<>();
            for (MiningSystem miningSystem : miningSystemList) {
                int IN_WORK_A = 0;
                int IN_IDLE_A = 0;
                int IN_CONSERVATION_A = 0;
                int IN_LIQUIDATION_A = 0;
                int IN_REPAIR_A = 0;
                List<Well> wellList = wellRepository.findAllByMiningSystemId(miningSystem.getId());

                for (Well well : wellList){
                    Optional<WellAction> action = wellActionRepository.findFirstByWellOrderByCreatedAtDesc(well);
                    if (action.isPresent()){
                        switch (action.get().getStatus()) {
                            case IN_WORK -> IN_WORK_A++;
                            case IN_IDLE -> IN_IDLE_A++;
                            case IN_LIQUIDATION -> IN_LIQUIDATION_A++;
                            case IN_CONSERVATION -> IN_CONSERVATION_A++;
                            case IN_REPAIR -> IN_REPAIR_A++;
                            default -> {
                            }
                        }
                    }


                }
                Map<String, Object> map = new HashMap<>();
                map.put("name",miningSystem.getName());
                map.put("id",miningSystem.getId());
                map.put(WellStatus.IN_WORK.name(), IN_WORK_A);
                map.put(WellStatus.IN_IDLE.name(), IN_IDLE_A);
                map.put(WellStatus.IN_CONSERVATION.name(), IN_CONSERVATION_A);
                map.put(WellStatus.IN_LIQUIDATION.name(), IN_LIQUIDATION_A);
                map.put(WellStatus.IN_REPAIR.name(), IN_REPAIR_A);
                stats.add(map);
//                result.put(miningSystem.getName(),map);
            }
            return converter.apiSuccess200(stats);
        }catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409();
        }
    }

    public ResponseEntity<?> addSpecial(User user, CollectionPoint collectionPoint, List<WellActionLite> wells) {
        try {
            for (WellActionLite wellLite : wells) {
                if (!wellRepository.existsByCollectionPointAndId(collectionPoint, wellLite.getWellId()))
                    return converter.apiError404("Well not found");
                else {
                    Optional<Well> well=wellRepository.findById(wellLite.getWellId());

                    if(well.isPresent()){
                        Optional<WellAction> wellAction=wellActionRepository.findFirstByWellOrderByCreatedAtDesc(well.get());

                        if (wellAction.isPresent()){
                            WellAction action=wellAction.get();
                            WellActionDto dto=WellActionDto
                                    .builder()
                                    .wellId(well.get().getId())
                                    .perforation_max(action.getPerforation_max())
                                    .perforation_min(action.getPerforation_min())
                                    .rpl(wellLite.getRpl()>0?wellLite.getRpl():0)
                                    .pressure(wellLite.getPressure()>0?wellLite.getPressure():0)
                                    .temperature(wellLite.getTemperature()>0?wellLite.getTemperature():0)
                                    .status(wellLite.getStatus())
                                    .build();
                            Optional<CollectionPointAction> collectionPointAction = collectionPointActionRepository.findFirstByCollectionPointOrderByCreatedAtDesc(collectionPoint);

                            if (wellLite.getStatus()==WellStatus.IN_WORK){
                                if (collectionPointAction.isPresent()&&wellLite.getPressure()<collectionPointAction.get().getPressure()){
                                    dto.setStatus(WellStatus.IN_IDLE);
                                }
                            }

                            if (wellLite.getStatus()==WellStatus.IN_IDLE){
                                if (collectionPointAction.isPresent()&&wellLite.getPressure()>=collectionPointAction.get().getPressure()){
                                    dto.setStatus(WellStatus.IN_WORK);
                                }
                            }
                            ResponseEntity<?> response = addManually(user, dto);
                            if (response.getStatusCode().value()!=201) return response;
                        }
                        else {
                            WellActionDto dto=WellActionDto
                                    .builder()
                                    .wellId(well.get().getId())
                                    .perforation_max(100)
                                    .perforation_min(200)
                                    .rpl(wellLite.getRpl()>0?wellLite.getRpl():0)
                                    .pressure(wellLite.getPressure()>0?wellLite.getPressure():0)
                                    .temperature(wellLite.getTemperature()>0?wellLite.getTemperature():0)
                                    .status(wellLite.getStatus())
                                    .build();
                            ResponseEntity<?> response = addManually(user, dto);
                            if (response.getStatusCode().value()!=201) return response;
                        }

                    }
                    else return converter.apiError404("Well not found");

                }
            }
            return converter.apiSuccess200();


        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409();
        }
    }

    public HttpEntity<?> editAction(WellActionDto dto) {
        try {
            if (dto.getActionId()== null) return converter.apiError400("Action Id is null");

            Optional<Well> well = wellRepository.findById(dto.getWellId());

            WellAction wellAction;
            Optional<WellAction> byId = wellActionRepository.findById(dto.getActionId());
            if (byId.isPresent()) {
                wellAction = byId.get();
                wellAction.setPressure(dto.getPressure());
                wellAction.setTemperature(dto.getTemperature());
                wellAction.setAverage_expend(dto.getAverage_expend());
                wellAction.setExpend(dto.getExpend());
                wellAction.setRpl(dto.getRpl());
                wellAction.setP_pkr(dto.getP_pkr());
                wellAction.setT_pkr(dto.getT_pkr());
                wellAction.setP_pr(dto.getP_pr());
                wellAction.setT_pr(dto.getT_pr());
                wellAction.setRo_otn(dto.getRo_otn());
                wellAction.setZ(dto.getZ());
                wellAction.setDelta(dto.getDelta());
                wellAction.setC(dto.getC());
                wellAction.setRo_gas(dto.getRo_gas());
                wellAction.setRo_air(dto.getRo_air());
                wellAction.setStatus(dto.getStatus());
                wellAction.setPerforation_min(dto.getPerforation_min());
                wellAction.setPerforation_max(dto.getPerforation_max());
                wellAction.setWell(well.get());

                WellAction save = wellActionRepository.save(wellAction);
                WellActionDto wellActionDto = (WellActionDto) converter.dto(save);
                return converter.apiSuccess200("Uppg action edited", wellActionDto);
            }
            return converter.apiError404("Uppg action not found");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409("Error editing Uppg action");
        }
    }

    public HttpEntity<?> deleteWellAction(Long id) {
        try {
            if (id != null) {
                Optional<WellAction> byId = wellActionRepository.findById(id);
                if (byId.isPresent()) {
                    wellActionRepository.delete(byId.get());
                    return converter.apiSuccess200("well action deleted");
                } else {
                    return converter.apiError404("well Action found");
                }
            }
            return converter.apiError400("Id null");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return converter.apiError409("Error in deleting uppg action", e);
        }

    }

    /** Auto **/

    //..... from MODBUS
    //... coming soon



    public void execute(Uppg uppg){
        Optional<UppgAction> expendUppgPerHour = uppgActionRepository.findFirstByUppgAndExpendGreaterThanOrderByCreatedAtDesc(uppg, 0);
        double expendUppg=8000000;
        if (expendUppgPerHour.isPresent()) {
            System.out.println("AAAAAAAAAAAAAAAA");
            System.out.println(expendUppgPerHour.get());
            expendUppg=expendUppgPerHour.get().getExpend()*1000;
        }
        double q_well=sumAllExpendByUppg(uppg);
        List<CollectionPoint> collectionPointList = collectionPointRepository.findAllByUppg(uppg);
        for (CollectionPoint collectionPoint : collectionPointList) {
            List<Well> wellList=wellRepository.findAllByCollectionPointOrderByIdAsc(collectionPoint);
            Optional<CollectionPointAction> pointAction = collectionPointActionRepository.findFirstByCollectionPointOrderByCreatedAtDesc(collectionPoint);
            double cpExpend=0;
            for (Well well : wellList){
                Optional<WellAction> action=wellActionRepository.findFirstByWellOrderByCreatedAtDesc(well);
                if (action.isPresent()){

                    if (pointAction.isPresent()&&(action.get().getPressure()<pointAction.get().getPressure()||pointAction.get().getPressure()==0)){
                        action.get().setExpend(0);
                        action.get().setRpl(rpl(action.get()));
                    }
                    else {
//                        action.get().setExpend(8000000*(action.get().getAverage_expend()/q_well));
                        action.get().setExpend(expendUppg*(action.get().getAverage_expend()/q_well));
                        action.get().setRpl(rpl(action.get()));
                    }
                    cpExpend+=action.get().getExpend();
                    wellActionRepository.save(action.get());
                }

            }
            if (pointAction.isPresent()&&pointAction.get().getPressure()!=0){
                pointAction.get().setExpend(cpExpend);
                collectionPointActionRepository.save(pointAction.get());
            }

        }
    }


    protected Double sumAllExpendByUppg(Uppg uppg){
        try {
            double sum=0;
            List<CollectionPoint> collectionPointList = collectionPointRepository.findAllByUppg(uppg);
            for (CollectionPoint collectionPoint : collectionPointList) {
                sum += sumAllExpendByCollectionPoint(collectionPoint);
            }
            return sum;

        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.toString());
            return 0.0;
        }
    }


    protected Double sumAllExpendByCollectionPoint(CollectionPoint collectionPoint){
        try {
            double sum=0.0;
            List<Well> wellList = wellRepository.findAllByCollectionPointOrderByIdAsc(collectionPoint);
            for (Well well : wellList) {
                Optional<WellAction> action = wellActionRepository.findFirstByWellOrderByCreatedAtDesc(well);
                if (action.isPresent()) {
                    sum += action.get().getAverage_expend();
                }
            }
            return sum;
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.toString());
            return 0.0;
        }
    }



}
