/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.dataflow.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.dataflow.rest.resource.TaskAppStatusResource;
import org.springframework.cloud.dataflow.server.service.TaskService;
import org.springframework.cloud.dataflow.server.service.ValidationStatus;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for operations on {@link ValidationStatus}.
 *
 * @author Glenn Renfro

 */
@RestController
@RequestMapping("/tasks/validation")
@ExposesResourceFor(TaskAppStatusResource.class)
public class TaskValidationController {

	private static final Logger logger = LoggerFactory.getLogger(TaskValidationController.class);

	/**
	 * The service that is responsible for validating tasks.
	 */
	private final TaskService taskService;

	/**
	 * Create a {@code TaskValidationController} that delegates to {@link TaskService}.
	 *
	 * @param taskService the task service to use
	 */
	public TaskValidationController(TaskService taskService) {
		Assert.notNull(taskService, "TaskService must not be null");
		this.taskService = taskService;
	}

	/**
	 * Return {@link TaskAppStatusResource} showing the validation status the apps in a task.
	 *
	 * @param name name of the task definition
	 * @return The status for the apps in a task definition.
	 */
	@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public TaskAppStatusResource validate(
			@PathVariable("name") String name) {
		ValidationStatus result = this.taskService.validateTask(name);
		return new Assembler().toResource(result);
	}

	/**
	 * {@link org.springframework.hateoas.ResourceAssembler} implementation that converts
	 * {@link ValidationStatus}s to {@link TaskAppStatusResource}s.
	 */
	class Assembler extends ResourceAssemblerSupport<ValidationStatus, TaskAppStatusResource> {

		public Assembler() {
			super(TaskValidationController.class, TaskAppStatusResource.class);
		}

		@Override
		public TaskAppStatusResource toResource(ValidationStatus entity) {
			return new TaskAppStatusResource(entity.getDefinitionName(), entity.getDefinitionDsl(), entity.getAppsStatuses());
		}
	}
}
