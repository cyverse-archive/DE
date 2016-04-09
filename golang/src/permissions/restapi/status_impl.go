package restapi

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

func serviceInfo() (*models.ServiceInfo, error) {
	var decoded SwaggerSpec

	// Extract the service info from the Swagger JSON.
	if err := json.Unmarshal(SwaggerJSON, &decoded); err != nil {
		return nil, fmt.Errorf("unable to decode the Swagger JSON: %s", err)
	}

	// Build the service info struct.
	info := decoded.Info
	return &models.ServiceInfo{&info.Description, &info.Title, &info.Version}, nil
}

func StatusHandler() middleware.Responder {
	info, err := serviceInfo()

	// TODO: move the parsing outside of the status handler itself.
	if err != nil {
		panic(err)
	}

	return status.NewGetOK().WithPayload(info)
}

// TODO: Build a function that returns a handler function. This will allow us to only parse
// the service info once, and it *should* ensure that any parsing errors that occur actually
// take down the server.
