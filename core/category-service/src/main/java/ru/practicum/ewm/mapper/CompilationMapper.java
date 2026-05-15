package ru.practicum.ewm.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.core.config.CommonMapperConfiguration;
import ru.practicum.ewm.dto.compilation.CompilationFullDto;
import ru.practicum.ewm.dto.compilation.CompilationUpdateDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.model.Compilation;

import java.util.Set;

@Mapper(config = CommonMapperConfiguration.class)
public interface CompilationMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "events", source = "events")
    @Mapping(target = "pinned", source = "dto.pinned")
    @Mapping(target = "title", source = "dto.title")
    Compilation toEntity(CompilationUpdateDto dto, Set<Long> events);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "events", source = "events")
    @Mapping(target = "pinned", source = "dto.pinned")
    @Mapping(target = "title", source = "dto.title")
    Compilation toEntityGeneral(@MappingTarget Compilation compilation, CompilationUpdateDto dto, Set<Long> events);

    @Mapping(target = "events", source = "events")
    CompilationFullDto toFullDto(Compilation compilation, Set<EventShortDto> events);
}