package impl

import (
	"encoding/json"
	"fmt"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"permissions/models"
	"permissions/restapi/operations/status"
)

type Info struct {
	Description string `json:"description"`
	Title       string `json:"title"`
	Version     string `json:"version"`
}

type SwaggerSpec struct {
	Info Info `json:"info"`
}

func serviceInfo(swaggerJson json.RawMessage) (*models.ServiceInfo, error) {
	var decoded SwaggerSpec

	// Extract the service info from the Swagger JSON.
	if err := json.Unmarshal(swaggerJson, &decoded); err != nil {
		return nil, fmt.Errorf("unable to decode the Swagger JSON: %s", err)
	}

	// Build the service info struct.
	info := decoded.Info
	return &models.ServiceInfo{&info.Description, &info.Title, &info.Version}, nil
}

func BuildStatusHandler(swaggerJson json.RawMessage) func() middleware.Responder {

	// Load the service info. Failure to do so will cause the service to abort.
	info, err := serviceInfo(swaggerJson)
	if err != nil {
		panic(err)
	}

	// Return the handler function.
	return func() middleware.Responder {
		return status.NewGetOK().WithPayload(info)
	}
}
