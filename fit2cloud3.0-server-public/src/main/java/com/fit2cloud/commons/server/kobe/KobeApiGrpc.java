package com.fit2cloud.commons.server.kobe;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.*;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.*;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 0.15.0)",
    comments = "Source: kobe.proto")
public class KobeApiGrpc {

  private KobeApiGrpc() {}

  public static final String SERVICE_NAME = "api.KobeApi";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Kobe.CreateProjectRequest,
      Kobe.CreateProjectResponse> METHOD_CREATE_PROJECT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "api.KobeApi", "CreateProject"),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.CreateProjectRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.CreateProjectResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Kobe.ListProjectRequest,
      Kobe.ListProjectResponse> METHOD_LIST_PROJECT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "api.KobeApi", "ListProject"),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.ListProjectRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.ListProjectResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Kobe.GetInventoryRequest,
      Kobe.GetInventoryResponse> METHOD_GET_INVENTORY =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "api.KobeApi", "GetInventory"),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.GetInventoryRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.GetInventoryResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Kobe.RunPlaybookRequest,
      Kobe.RunPlaybookResult> METHOD_RUN_PLAYBOOK =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "api.KobeApi", "RunPlaybook"),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.RunPlaybookRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.RunPlaybookResult.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Kobe.RunAdhocRequest,
      Kobe.RunAdhocResult> METHOD_RUN_ADHOC =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "api.KobeApi", "RunAdhoc"),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.RunAdhocRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.RunAdhocResult.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Kobe.WatchRequest,
      Kobe.WatchStream> METHOD_WATCH_RESULT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
          generateFullMethodName(
              "api.KobeApi", "WatchResult"),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.WatchRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.WatchStream.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Kobe.GetResultRequest,
      Kobe.GetResultResponse> METHOD_GET_RESULT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "api.KobeApi", "GetResult"),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.GetResultRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.GetResultResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Kobe.ListResultRequest,
      Kobe.ListResultResponse> METHOD_LIST_RESULT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "api.KobeApi", "ListResult"),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.ListResultRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(Kobe.ListResultResponse.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static KobeApiStub newStub(io.grpc.Channel channel) {
    return new KobeApiStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static KobeApiBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new KobeApiBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static KobeApiFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new KobeApiFutureStub(channel);
  }

  /**
   */
  @Deprecated public static interface KobeApi {

    /**
     */
    public void createProject(Kobe.CreateProjectRequest request,
                              io.grpc.stub.StreamObserver<Kobe.CreateProjectResponse> responseObserver);

    /**
     */
    public void listProject(Kobe.ListProjectRequest request,
                            io.grpc.stub.StreamObserver<Kobe.ListProjectResponse> responseObserver);

    /**
     */
    public void getInventory(Kobe.GetInventoryRequest request,
                             io.grpc.stub.StreamObserver<Kobe.GetInventoryResponse> responseObserver);

    /**
     */
    public void runPlaybook(Kobe.RunPlaybookRequest request,
                            io.grpc.stub.StreamObserver<Kobe.RunPlaybookResult> responseObserver);

    /**
     */
    public void runAdhoc(Kobe.RunAdhocRequest request,
                         io.grpc.stub.StreamObserver<Kobe.RunAdhocResult> responseObserver);

    /**
     */
    public void watchResult(Kobe.WatchRequest request,
                            io.grpc.stub.StreamObserver<Kobe.WatchStream> responseObserver);

    /**
     */
    public void getResult(Kobe.GetResultRequest request,
                          io.grpc.stub.StreamObserver<Kobe.GetResultResponse> responseObserver);

    /**
     */
    public void listResult(Kobe.ListResultRequest request,
                           io.grpc.stub.StreamObserver<Kobe.ListResultResponse> responseObserver);
  }

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1469")
  public static abstract class KobeApiImplBase implements KobeApi, io.grpc.BindableService {

    @Override
    public void createProject(Kobe.CreateProjectRequest request,
        io.grpc.stub.StreamObserver<Kobe.CreateProjectResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CREATE_PROJECT, responseObserver);
    }

    @Override
    public void listProject(Kobe.ListProjectRequest request,
        io.grpc.stub.StreamObserver<Kobe.ListProjectResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_LIST_PROJECT, responseObserver);
    }

    @Override
    public void getInventory(Kobe.GetInventoryRequest request,
        io.grpc.stub.StreamObserver<Kobe.GetInventoryResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_INVENTORY, responseObserver);
    }

    @Override
    public void runPlaybook(Kobe.RunPlaybookRequest request,
        io.grpc.stub.StreamObserver<Kobe.RunPlaybookResult> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_RUN_PLAYBOOK, responseObserver);
    }

    @Override
    public void runAdhoc(Kobe.RunAdhocRequest request,
        io.grpc.stub.StreamObserver<Kobe.RunAdhocResult> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_RUN_ADHOC, responseObserver);
    }

    @Override
    public void watchResult(Kobe.WatchRequest request,
        io.grpc.stub.StreamObserver<Kobe.WatchStream> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_WATCH_RESULT, responseObserver);
    }

    @Override
    public void getResult(Kobe.GetResultRequest request,
        io.grpc.stub.StreamObserver<Kobe.GetResultResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_RESULT, responseObserver);
    }

    @Override
    public void listResult(Kobe.ListResultRequest request,
        io.grpc.stub.StreamObserver<Kobe.ListResultResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_LIST_RESULT, responseObserver);
    }

    @Override public io.grpc.ServerServiceDefinition bindService() {
      return KobeApiGrpc.bindService(this);
    }
  }

  /**
   */
  @Deprecated public static interface KobeApiBlockingClient {

    /**
     */
    public Kobe.CreateProjectResponse createProject(Kobe.CreateProjectRequest request);

    /**
     */
    public Kobe.ListProjectResponse listProject(Kobe.ListProjectRequest request);

    /**
     */
    public Kobe.GetInventoryResponse getInventory(Kobe.GetInventoryRequest request);

    /**
     */
    public Kobe.RunPlaybookResult runPlaybook(Kobe.RunPlaybookRequest request);

    /**
     */
    public Kobe.RunAdhocResult runAdhoc(Kobe.RunAdhocRequest request);

    /**
     */
    public java.util.Iterator<Kobe.WatchStream> watchResult(
            Kobe.WatchRequest request);

    /**
     */
    public Kobe.GetResultResponse getResult(Kobe.GetResultRequest request);

    /**
     */
    public Kobe.ListResultResponse listResult(Kobe.ListResultRequest request);
  }

  /**
   */
  @Deprecated public static interface KobeApiFutureClient {

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Kobe.CreateProjectResponse> createProject(
            Kobe.CreateProjectRequest request);

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Kobe.ListProjectResponse> listProject(
            Kobe.ListProjectRequest request);

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Kobe.GetInventoryResponse> getInventory(
            Kobe.GetInventoryRequest request);

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Kobe.RunPlaybookResult> runPlaybook(
            Kobe.RunPlaybookRequest request);

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Kobe.RunAdhocResult> runAdhoc(
            Kobe.RunAdhocRequest request);

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Kobe.GetResultResponse> getResult(
            Kobe.GetResultRequest request);

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Kobe.ListResultResponse> listResult(
            Kobe.ListResultRequest request);
  }

  public static class KobeApiStub extends io.grpc.stub.AbstractStub<KobeApiStub>
      implements KobeApi {
    private KobeApiStub(io.grpc.Channel channel) {
      super(channel);
    }

    private KobeApiStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected KobeApiStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new KobeApiStub(channel, callOptions);
    }

    @Override
    public void createProject(Kobe.CreateProjectRequest request,
        io.grpc.stub.StreamObserver<Kobe.CreateProjectResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CREATE_PROJECT, getCallOptions()), request, responseObserver);
    }

    @Override
    public void listProject(Kobe.ListProjectRequest request,
        io.grpc.stub.StreamObserver<Kobe.ListProjectResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_LIST_PROJECT, getCallOptions()), request, responseObserver);
    }

    @Override
    public void getInventory(Kobe.GetInventoryRequest request,
        io.grpc.stub.StreamObserver<Kobe.GetInventoryResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_INVENTORY, getCallOptions()), request, responseObserver);
    }

    @Override
    public void runPlaybook(Kobe.RunPlaybookRequest request,
        io.grpc.stub.StreamObserver<Kobe.RunPlaybookResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_RUN_PLAYBOOK, getCallOptions()), request, responseObserver);
    }

    @Override
    public void runAdhoc(Kobe.RunAdhocRequest request,
        io.grpc.stub.StreamObserver<Kobe.RunAdhocResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_RUN_ADHOC, getCallOptions()), request, responseObserver);
    }

    @Override
    public void watchResult(Kobe.WatchRequest request,
        io.grpc.stub.StreamObserver<Kobe.WatchStream> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_WATCH_RESULT, getCallOptions()), request, responseObserver);
    }

    @Override
    public void getResult(Kobe.GetResultRequest request,
        io.grpc.stub.StreamObserver<Kobe.GetResultResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_RESULT, getCallOptions()), request, responseObserver);
    }

    @Override
    public void listResult(Kobe.ListResultRequest request,
        io.grpc.stub.StreamObserver<Kobe.ListResultResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_LIST_RESULT, getCallOptions()), request, responseObserver);
    }
  }

  public static class KobeApiBlockingStub extends io.grpc.stub.AbstractStub<KobeApiBlockingStub>
      implements KobeApiBlockingClient {
    private KobeApiBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private KobeApiBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected KobeApiBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new KobeApiBlockingStub(channel, callOptions);
    }

    @Override
    public Kobe.CreateProjectResponse createProject(Kobe.CreateProjectRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CREATE_PROJECT, getCallOptions(), request);
    }

    @Override
    public Kobe.ListProjectResponse listProject(Kobe.ListProjectRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_LIST_PROJECT, getCallOptions(), request);
    }

    @Override
    public Kobe.GetInventoryResponse getInventory(Kobe.GetInventoryRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_INVENTORY, getCallOptions(), request);
    }

    @Override
    public Kobe.RunPlaybookResult runPlaybook(Kobe.RunPlaybookRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_RUN_PLAYBOOK, getCallOptions(), request);
    }

    @Override
    public Kobe.RunAdhocResult runAdhoc(Kobe.RunAdhocRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_RUN_ADHOC, getCallOptions(), request);
    }

    @Override
    public java.util.Iterator<Kobe.WatchStream> watchResult(
        Kobe.WatchRequest request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_WATCH_RESULT, getCallOptions(), request);
    }

    @Override
    public Kobe.GetResultResponse getResult(Kobe.GetResultRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_RESULT, getCallOptions(), request);
    }

    @Override
    public Kobe.ListResultResponse listResult(Kobe.ListResultRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_LIST_RESULT, getCallOptions(), request);
    }
  }

  public static class KobeApiFutureStub extends io.grpc.stub.AbstractStub<KobeApiFutureStub>
      implements KobeApiFutureClient {
    private KobeApiFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private KobeApiFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected KobeApiFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new KobeApiFutureStub(channel, callOptions);
    }

    @Override
    public com.google.common.util.concurrent.ListenableFuture<Kobe.CreateProjectResponse> createProject(
        Kobe.CreateProjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CREATE_PROJECT, getCallOptions()), request);
    }

    @Override
    public com.google.common.util.concurrent.ListenableFuture<Kobe.ListProjectResponse> listProject(
        Kobe.ListProjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_LIST_PROJECT, getCallOptions()), request);
    }

    @Override
    public com.google.common.util.concurrent.ListenableFuture<Kobe.GetInventoryResponse> getInventory(
        Kobe.GetInventoryRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_INVENTORY, getCallOptions()), request);
    }

    @Override
    public com.google.common.util.concurrent.ListenableFuture<Kobe.RunPlaybookResult> runPlaybook(
        Kobe.RunPlaybookRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_RUN_PLAYBOOK, getCallOptions()), request);
    }

    @Override
    public com.google.common.util.concurrent.ListenableFuture<Kobe.RunAdhocResult> runAdhoc(
        Kobe.RunAdhocRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_RUN_ADHOC, getCallOptions()), request);
    }

    @Override
    public com.google.common.util.concurrent.ListenableFuture<Kobe.GetResultResponse> getResult(
        Kobe.GetResultRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_RESULT, getCallOptions()), request);
    }

    @Override
    public com.google.common.util.concurrent.ListenableFuture<Kobe.ListResultResponse> listResult(
        Kobe.ListResultRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_LIST_RESULT, getCallOptions()), request);
    }
  }

  @Deprecated public static abstract class AbstractKobeApi extends KobeApiImplBase {}

  private static final int METHODID_CREATE_PROJECT = 0;
  private static final int METHODID_LIST_PROJECT = 1;
  private static final int METHODID_GET_INVENTORY = 2;
  private static final int METHODID_RUN_PLAYBOOK = 3;
  private static final int METHODID_RUN_ADHOC = 4;
  private static final int METHODID_WATCH_RESULT = 5;
  private static final int METHODID_GET_RESULT = 6;
  private static final int METHODID_LIST_RESULT = 7;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final KobeApi serviceImpl;
    private final int methodId;

    public MethodHandlers(KobeApi serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_PROJECT:
          serviceImpl.createProject((Kobe.CreateProjectRequest) request,
              (io.grpc.stub.StreamObserver<Kobe.CreateProjectResponse>) responseObserver);
          break;
        case METHODID_LIST_PROJECT:
          serviceImpl.listProject((Kobe.ListProjectRequest) request,
              (io.grpc.stub.StreamObserver<Kobe.ListProjectResponse>) responseObserver);
          break;
        case METHODID_GET_INVENTORY:
          serviceImpl.getInventory((Kobe.GetInventoryRequest) request,
              (io.grpc.stub.StreamObserver<Kobe.GetInventoryResponse>) responseObserver);
          break;
        case METHODID_RUN_PLAYBOOK:
          serviceImpl.runPlaybook((Kobe.RunPlaybookRequest) request,
              (io.grpc.stub.StreamObserver<Kobe.RunPlaybookResult>) responseObserver);
          break;
        case METHODID_RUN_ADHOC:
          serviceImpl.runAdhoc((Kobe.RunAdhocRequest) request,
              (io.grpc.stub.StreamObserver<Kobe.RunAdhocResult>) responseObserver);
          break;
        case METHODID_WATCH_RESULT:
          serviceImpl.watchResult((Kobe.WatchRequest) request,
              (io.grpc.stub.StreamObserver<Kobe.WatchStream>) responseObserver);
          break;
        case METHODID_GET_RESULT:
          serviceImpl.getResult((Kobe.GetResultRequest) request,
              (io.grpc.stub.StreamObserver<Kobe.GetResultResponse>) responseObserver);
          break;
        case METHODID_LIST_RESULT:
          serviceImpl.listResult((Kobe.ListResultRequest) request,
              (io.grpc.stub.StreamObserver<Kobe.ListResultResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    return new io.grpc.ServiceDescriptor(SERVICE_NAME,
        METHOD_CREATE_PROJECT,
        METHOD_LIST_PROJECT,
        METHOD_GET_INVENTORY,
        METHOD_RUN_PLAYBOOK,
        METHOD_RUN_ADHOC,
        METHOD_WATCH_RESULT,
        METHOD_GET_RESULT,
        METHOD_LIST_RESULT);
  }

  @Deprecated public static io.grpc.ServerServiceDefinition bindService(
      final KobeApi serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          METHOD_CREATE_PROJECT,
          asyncUnaryCall(
            new MethodHandlers<
              Kobe.CreateProjectRequest,
              Kobe.CreateProjectResponse>(
                serviceImpl, METHODID_CREATE_PROJECT)))
        .addMethod(
          METHOD_LIST_PROJECT,
          asyncUnaryCall(
            new MethodHandlers<
              Kobe.ListProjectRequest,
              Kobe.ListProjectResponse>(
                serviceImpl, METHODID_LIST_PROJECT)))
        .addMethod(
          METHOD_GET_INVENTORY,
          asyncUnaryCall(
            new MethodHandlers<
              Kobe.GetInventoryRequest,
              Kobe.GetInventoryResponse>(
                serviceImpl, METHODID_GET_INVENTORY)))
        .addMethod(
          METHOD_RUN_PLAYBOOK,
          asyncUnaryCall(
            new MethodHandlers<
              Kobe.RunPlaybookRequest,
              Kobe.RunPlaybookResult>(
                serviceImpl, METHODID_RUN_PLAYBOOK)))
        .addMethod(
          METHOD_RUN_ADHOC,
          asyncUnaryCall(
            new MethodHandlers<
              Kobe.RunAdhocRequest,
              Kobe.RunAdhocResult>(
                serviceImpl, METHODID_RUN_ADHOC)))
        .addMethod(
          METHOD_WATCH_RESULT,
          asyncServerStreamingCall(
            new MethodHandlers<
              Kobe.WatchRequest,
              Kobe.WatchStream>(
                serviceImpl, METHODID_WATCH_RESULT)))
        .addMethod(
          METHOD_GET_RESULT,
          asyncUnaryCall(
            new MethodHandlers<
              Kobe.GetResultRequest,
              Kobe.GetResultResponse>(
                serviceImpl, METHODID_GET_RESULT)))
        .addMethod(
          METHOD_LIST_RESULT,
          asyncUnaryCall(
            new MethodHandlers<
              Kobe.ListResultRequest,
              Kobe.ListResultResponse>(
                serviceImpl, METHODID_LIST_RESULT)))
        .build();
  }
}
