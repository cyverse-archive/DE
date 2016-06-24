package permissions

// This file was generated by the swagger tool.
// Editing this file might prove futile when you re-run the swagger generate command

import (
	"net/http"

	"github.com/go-swagger/go-swagger/httpkit"

	"permissions/models"
)

/*RevokePermissionOK OK

swagger:response revokePermissionOK
*/
type RevokePermissionOK struct {
}

// NewRevokePermissionOK creates RevokePermissionOK with default headers values
func NewRevokePermissionOK() *RevokePermissionOK {
	return &RevokePermissionOK{}
}

// WriteResponse to the client
func (o *RevokePermissionOK) WriteResponse(rw http.ResponseWriter, producer httpkit.Producer) {

	rw.WriteHeader(200)
}

/*RevokePermissionNotFound Not Found

swagger:response revokePermissionNotFound
*/
type RevokePermissionNotFound struct {

	// In: body
	Payload *models.ErrorOut `json:"body,omitempty"`
}

// NewRevokePermissionNotFound creates RevokePermissionNotFound with default headers values
func NewRevokePermissionNotFound() *RevokePermissionNotFound {
	return &RevokePermissionNotFound{}
}

// WithPayload adds the payload to the revoke permission not found response
func (o *RevokePermissionNotFound) WithPayload(payload *models.ErrorOut) *RevokePermissionNotFound {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the revoke permission not found response
func (o *RevokePermissionNotFound) SetPayload(payload *models.ErrorOut) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *RevokePermissionNotFound) WriteResponse(rw http.ResponseWriter, producer httpkit.Producer) {

	rw.WriteHeader(404)
	if o.Payload != nil {
		if err := producer.Produce(rw, o.Payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}

/*RevokePermissionInternalServerError revoke permission internal server error

swagger:response revokePermissionInternalServerError
*/
type RevokePermissionInternalServerError struct {

	// In: body
	Payload *models.ErrorOut `json:"body,omitempty"`
}

// NewRevokePermissionInternalServerError creates RevokePermissionInternalServerError with default headers values
func NewRevokePermissionInternalServerError() *RevokePermissionInternalServerError {
	return &RevokePermissionInternalServerError{}
}

// WithPayload adds the payload to the revoke permission internal server error response
func (o *RevokePermissionInternalServerError) WithPayload(payload *models.ErrorOut) *RevokePermissionInternalServerError {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the revoke permission internal server error response
func (o *RevokePermissionInternalServerError) SetPayload(payload *models.ErrorOut) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *RevokePermissionInternalServerError) WriteResponse(rw http.ResponseWriter, producer httpkit.Producer) {

	rw.WriteHeader(500)
	if o.Payload != nil {
		if err := producer.Produce(rw, o.Payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}