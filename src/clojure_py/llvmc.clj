(ns clojure-py.llvmc
  (:import (com.sun.jna Native Pointer Memory))
  (:require [clojure.java.shell :as shell]
            [clojure.string :as string]))

(def ^:dynamic *lib* 'LLVM-3.1)


(defn get-function [s]
  `(com.sun.jna.Function/getFunction ~(name *lib*) ~(name s)))

(defn debug [s]
  (println s)

  s)

(def debug-mode false)

(defmacro defnative
  [return-type function-symbol]
  `(let [func# ~(get-function function-symbol)]
     (defn ~(symbol (name function-symbol))
       [& args#]
       (let [r# (.invoke func# ~return-type (to-array args#))]
         (when debug-mode
           (println "After " ~(name function-symbol))
           (System/gc)
           (System/runFinalization)
           (Thread/sleep 500))
         r#))))

(defn new-pointer []
  (let [p (Memory. Pointer/SIZE)]
   (.clear p)
    p))


(defn to-pointers [& args]
  (let [arr (make-array Pointer (count args))]
    (loop [a args
           c 0]
      (if a
        (do (aset arr c (first a))
            (recur (next a) (inc c)))
        arr))))


(def LLVMCCallConv 0)
(def LLVMFastCallConv 8)
(def LLVMColdCallConv 9)
(def LLVMX86StdcallCallConv 64)
(def LLVMX86FastcallCallConv 65)
(defnative Integer LLVMSetFunctionCallConv)
(defnative Integer LLVMFindFunction)

(defnative Pointer LLVMAppendBasicBlock)
(defnative Pointer LLVMCreateBuilder)

(defnative Pointer LLVMGetParam)

(defnative Integer LLVMLinkInJIT)
'(defnative Integer LLVMInitializeNativeTarget)

(defnative Pointer LLVMModuleCreateWithName)

(defnative Pointer LLVMInt32Type)
(defnative Pointer LLVMFunctionType)

(defnative Pointer LLVMAddFunction)

(defnative Integer LLVMPositionBuilderAtEnd)

(defnative Boolean LLVMVerifyModule)

(def LLVMAbortProcessAction 0)
(def LLVMPrintMessageAction 1)
(def LLVMReturnStatusAction 2)

(defnative Pointer LLVMCreateModuleProviderForExistingModule)

(defnative Integer LLVMDisposeMessage)
(defnative Integer LLVMCreateJITCompiler)
(defnative Integer LLVMCreateInterpreterForModule)
(defnative Pointer LLVMCreatePassManager)
(defnative Pointer LLVMGetExecutionEngineTargetData)
(defnative Integer LLVMAddTargetData)
(defnative Integer LLVMRunPassManager)
(defnative Integer LLVMDumpModule)
(defnative Integer LLVMDisposePassManager)
(defnative Integer LLVMDisposeExecutionEngine)
(defnative Integer LLVMBuildRet)

(defnative Integer LLVMLinkInJIT)
(defnative Integer LLVMLinkInInterpreter)
(defnative Integer LLVMInitializeX86Target)
(defnative Integer LLVMInitializeX86TargetInfo)
(defnative Integer LLVMInitializeX86TargetMC)
(defnative Pointer LLVMRunFunction)
(defnative Boolean LLVMFindFunction)
(defnative Pointer LLVMCreateGenericValueOfInt)
(defnative Integer LLVMGenericValueToInt)
(defnative Pointer LLVMBuildAdd)
(defnative Pointer LLVMBuildSub)
(defnative Pointer LLVMConstInt)
(defnative Pointer LLVMBuildICmp)
(defnative Pointer LLVMIntType)
(defnative Pointer LLVMBuildCondBr)
(defnative Pointer LLVMBuildPhi)
(defnative Integer LLVMAddIncoming)
(defnative Pointer LLVMTypeOf)
(defnative Integer LLVMCountParamTypes)
(defnative Integer LLVMGetTypeKind)
(defnative Integer LLVMDisposeGenericValue)
(defnative Integer LLVMDisposeBuilder)
(defnative Pointer LLVMBuildBr)
(defnative Pointer LLVMBuildCall)
(defnative Pointer LLVMBuildAlloca)
(defnative Pointer LLVMBuildLoad)
(defnative Pointer LLVMBuildStore)

(defnative Integer LLVMAddConstantPropagationPass)
(defnative Integer LLVMAddInstructionCombiningPass)
(defnative Integer LLVMAddPromoteMemoryToRegisterPass)
(defnative Integer LLVMAddGVNPass)
(defnative Integer LLVMAddCFGSimplificationPass)
(defnative Pointer LLVMBuildArrayMalloc)
(defnative Pointer LLVMBuildGEP)
(defnative Pointer LLVMBuildBitCast)
(defnative Pointer LLVMConstString)
(defnative Pointer LLVMConstInt)
(defnative Integer LLVMCountStructElementTypes)
(defnative Pointer LLVMConstPointerCast)
(defnative Pointer LLVMGetStructElementTypes)
(defnative Integer LLVMGetTypeKind)
(defnative Pointer LLVMConstPointerNull)
(defnative Pointer LLVMInt64Type)
(defnative Pointer LLVMStructType)
(defnative Pointer LLVMArrayType)
(defnative Pointer LLVMDumpValue)
(defnative Integer LLVMGetArrayLength)
(defnative Pointer LLVMGetElementType)
(defnative Pointer LLVMConstArray)
(defnative Pointer LLVMConstString)
(defnative Pointer LLVMConstStruct)
(defnative Pointer LLVMConstGEP)
(defnative Pointer LLVMConstBitCast)
(defnative Integer LLVMCountParams)
(defnative Pointer LLVMAddGlobal)
(defnative Integer LLVMSetInitializer)
(defnative Integer LLVMWriteBitcodeToFile)
(defnative Pointer LLVMGetNamedGlobal)
(defnative Pointer LLVMGetNamedFunction)
(defnative Pointer LLVMInt8Type)
(defnative Pointer LLVMPointerType)
(defnative Integer LLVMSetLinkage)
(defnative Integer LLVMGetIntTypeWidth)


(def ^:dynamic *module* (LLVMModuleCreateWithName "tmp"))
(def ^:dynamic *fn*)
(def ^:dynamic *locals*)
(def ^:dynamic *builder*)
(def ^:dynamic *block*)


(def LLVMIntEQ 32)

(defmacro defenum
  [nm defs]
  (list* 'do
        `(def ~nm {:idx ~(zipmap (range)
                                   (map (comp keyword name) defs))
                     :defs ~(zipmap (map (comp keyword name) defs)
                                    (range))})
        (map-indexed (fn [idx d]
                       `(def ~d ~idx))
                     defs)))

(defenum LLVMTypeKind
  [LLVMVoidTypeKind
   LLVMHalfTypeKind
   LLVMFloatTypeKind
   LLVMDoubleTypeKind
   LLVMX86_FP80TypeKind
   LLVMFP128TypeKind
   LLVMPPC_FP128TypeKind
   LLVMLabelTypeKind
   LLVMIntegerTypeKind
   LLVMFunctionTypeKind
   LLVMStructTypeKind
   LLVMArrayTypeKind
   LLVMPointerTypeKind
   LLVMVectorTypeKind
   LLVMMetadataTypeKind
   LLVMX86_MMXTypeKind])

(defenum LLVMCodeGentFileType
  [LLVMAssemblyFile
   LLVMObjectFile])

(defenum LLVMRelocMode
  [LLVMRelocDefault
   LLVMRelocStatic
   LLVMRelocPIC
   LLVMRelocDynamicNoPIC])

(defenum LLVMCodeGenOptLevel
  [LLVMCodeGenLevelNone
   LLVMCodeGenLevelLess
   LLVMCodeGenLevelDefault
   LLVMCodeGenLevelAggressive])

(defenum LLVMCodeModel
  [LLVMCodeModelDefault
   LLVMCodeModelJITDefault
   LLVMCodeModelSmall
   LLVMCodeModelKernel
   LLVMCodeModelMedium
   LLVMCodeModelLarge])


(defenum LLVMLinkage
  [LLVMExternalLinkage,    ; Externally visible function 
   LLVMAvailableExternallyLinkage,
   LLVMLinkOnceAnyLinkage, ; Keep one copy of function when linking (inline)
   LLVMLinkOnceODRLinkage, ; Same, but only replaced by something equivalent. 
   LLVMWeakAnyLinkage,     ; Keep one copy of function when linking (weak) 
   LLVMWeakODRLinkage,     ; Same, but only replaced by something equivalent. 
   LLVMAppendingLinkage,   ; Special purpose, only applies to global arrays 
   LLVMInternalLinkage,    ; Rename collisions when linking (static functions)
   LLVMPrivateLinkage,     ; Like Internal, but omit from symbol table 
   LLVMDLLImportLinkage,   ; Function to be imported from DLL 
   LLVMDLLExportLinkage,   ; Function to be accessible from DLL 
   LLVMExternalWeakLinkage,; ExternalWeak linkage description 
   LLVMGhostLinkage,       ; Obsolete 
   LLVMCommonLinkage,      ; Tentative definitions 
   LLVMLinkerPrivateLinkage, ; Like Private, but linker removes. 
   LLVMLinkerPrivateWeakLinkage, ; Like LinkerPrivate, but is weak. 
   LLVMLinkerPrivateWeakDefAutoLinkage]) ; Like LinkerPrivateWeak, but possibly hidden. 


(defn init-target []
  (LLVMLinkInJIT)
  (LLVMLinkInInterpreter)
  (LLVMInitializeX86TargetInfo)
  (LLVMInitializeX86Target)
  (LLVMInitializeX86TargetMC))

(def kw->linkage
  {:extern LLVMExternalLinkage})

(declare llvm-type
         )

(defmulti llvm-type-to-data (fn [tp]
                              (get-in LLVMTypeKind [:idx (LLVMGetTypeKind tp)])))

(defmethod llvm-type-to-data :LLVMPointerTypeKind
  [tp]
  {:type :*
   :etype (llvm-type-to-data (LLVMGetElementType tp))})

(defmethod llvm-type-to-data :LLVMIntegerTypeKind
  [tp]
  {:type :int
   :width (LLVMGetIntTypeWidth tp)})

(defmethod llvm-type-to-data :LLVMArrayTypeKind
  [tp]
  {:type :array
   :etype (llvm-type-to-data (LLVMGetElementType tp))
   :size (LLVMGetArrayLength tp)})

(defmethod llvm-type-to-data :LLVMStructTypeKind
  [tp]
  (let [cnt (LLVMCountStructElementTypes tp)
        arr (make-array Pointer cnt)]
    (LLVMGetStructElementTypes tp arr)
    {:type :struct
     :members (mapv llvm-type-to-data arr)}))

(defmethod llvm-type-to-data :LLVMFunctionTypeKind
  [tp]
  :fn*)

(defmulti encode-const (fn [tp v]
                         (get-in LLVMTypeKind [:idx (LLVMGetTypeKind tp)])))

(defn const-string-array [s]
  (let [ar (into-array Pointer (map #(LLVMConstInt (llvm-type :i8) % false)
                                    (concat s [0])))
        llvm-ar (LLVMConstArray (llvm-type :i8)
                        ar
                        (count ar))
        idx (into-array Pointer
                        [(LLVMConstInt (llvm-type :int) 0)])
        gbl (LLVMAddGlobal *module* (llvm-type {:type :array
                                                :size (count ar)
                                                :etype :i8})
                           (name (gensym "str_")))
        casted (LLVMConstBitCast gbl
                                 (llvm-type :i8*))]
    (LLVMSetInitializer gbl llvm-ar)

    casted
    ))

(defmethod encode-const :LLVMPointerTypeKind
  [tp v]
  (cond
   (map? v) (LLVMGetNamedFunction *module* (:global v))
   (string? v) (const-string-array v) #_(LLVMConstString v (count v) false)
   (instance? Pointer v) v
   (nil? v) (LLVMConstPointerNull tp)
   :else (assert false (str "Can't create pointer from " v))))

(defmethod encode-const :LLVMFunctionTypeKind
  [tp v]
  (assert (or (string? v)
              (nil? v)))
  (println "=========" tp (LLVMGetTypeKind tp))
  (if (nil? v)
    (LLVMConstPointerNull tp)
    (let [fnc (LLVMGetNamedFunction *module* v)]
      (assert fnc (str "Couldn't find " v))
      fnc)))

(defmethod encode-const :LLVMIntegerTypeKind
  [tp v]
  (LLVMConstInt tp v true))

(defmethod encode-const :LLVMArrayTypeKind
  [tp v]
  (println (llvm-type-to-data tp))
  (let [alen (LLVMGetArrayLength tp)
        atp (LLVMGetElementType tp)
        els (into-array Pointer
                                    (debug (map encode-const
                                                (repeat atp)
                                                v)))]
    (assert (= alen (count v)) (str "Wrong number of elements to constant array" alen " got " (count v)))
    (println "---------------------- " v)
    (LLVMConstArray atp
                    els
                    alen)))

(defmethod encode-const :LLVMStructTypeKind
  [tp v]
  (let [cnt (LLVMCountStructElementTypes tp)
        arr (make-array Pointer cnt)]
    (assert (= cnt (count v)))
    (LLVMGetStructElementTypes tp arr)
    (LLVMConstStruct (into-array Pointer
                                 (debug (map encode-const arr v)))
                     cnt
                     false)))


(println "Init LLVM")

(defprotocol ILLVMTypeDesc
  (llvm-type [this]))


(defmulti -llvm-type-kw identity)
(defmethod -llvm-type-kw :int
  [kw]
  (LLVMIntType 32))

(defmethod -llvm-type-kw :long
  [kw]
  (LLVMIntType 64))

(defmethod -llvm-type-kw :i8
  [kw]
  (LLVMInt8Type))

(defmethod -llvm-type-kw :i8*
  [kw]
  (llvm-type
   {:type :*
    :etype :i8}))


(defmulti -llvm-type-assoc :type)
(defmethod -llvm-type-assoc :struct
  [{:keys [members packed]}]
  (assert members)
  (let [ele (into-array Pointer (map llvm-type members))
        packed (or packed false)]
    (LLVMStructType ele (count ele) packed)))


(defmethod -llvm-type-assoc :*
  [{:keys [etype]}]
  (LLVMPointerType (llvm-type etype) 0))

(defmethod -llvm-type-assoc :array
  [{:keys [size etype]}]
  (assert (and size etype))
  (LLVMArrayType (llvm-type etype) size))

(defmethod -llvm-type-assoc :fn
  [{:keys [ret args vararg?]}]
  (LLVMFunctionType (llvm-type ret)
                    (into-array Pointer (map llvm-type args))
                    (count args)
                    (or vararg? false)))

(defmethod -llvm-type-assoc :fn*
  [mp]
  (LLVMPointerType (-llvm-type-assoc (assoc mp :type :fn)) 0))



(extend-protocol ILLVMTypeDesc
  clojure.lang.Keyword
  (llvm-type [this]
    (-llvm-type-kw this))
  clojure.lang.Associative
  (llvm-type [this]
    (-llvm-type-assoc this)))

(defn nstruct [name types & opts]
  (let [opts (apply hash-map opts)]))

(def genname (comp name gensym))



(defn value-at [ptr]
  (.getPointer ptr 0))



(defmulti stub-global :op)

(defmethod stub-global :global
  [{:keys [name type linkage]}]
  (assert (and name type))
  (let [tp (llvm-type type)
        gbl (LLVMAddGlobal *module* tp name)]
    (when linkage
      (LLVMSetLinkage gbl (kw->linkage linkage)))
    gbl))

(defmethod stub-global :fn
  [{:keys [name type linkage]}]
  (let [tp (llvm-type type)
        gbl (LLVMAddFunction *module* name tp)]
    (when linkage
      (LLVMSetLinkage gbl (kw->linkage linkage)))
    gbl))


(defmulti compile :op)

(defmethod compile :const
  [{:keys [value type]}]
  (let [tp (llvm-type type)]
    (encode-const tp value)))

(defmethod compile :global
  [{:keys [type value name]}]
  (let [val (LLVMGetNamedGlobal *module* name)]
    (println "========== init ==========" name type)
    (LLVMSetInitializer val (encode-const (llvm-type type) value))))

(defmethod compile :call
  [{:keys [fn args]}]
  (let [fnc (LLVMGetNamedFunction *module* fn)]
    (LLVMBuildCall *builder*
                   fnc
                   (into-array Pointer
                               (map compile args))
                   (count args)
                   (genname "call_"))))

(defmethod compile :do
  [{:keys [body]}]
  (doseq [x (butlast body)]
    (compile x))
  (compile (last body)))

(defmethod compile :get-global
  [{:keys [name]}]
  (LLVMGetNamedGlobal *module* name))

(defmethod compile :bitcast
  [{:keys [type value]}]
  (LLVMBuildBitCast *builder*
                    (compile value)
                    (llvm-type type)
                    (name (gensym "bitcast_"))))

(defmethod compile :fn
  [{:keys [type args name body]}]
  (when body
    (let [fnc (LLVMGetNamedFunction *module* name)
          pcnt (LLVMCountParams fnc)
          newargs (map (fn [s idx]
                         [s (LLVMGetParam fnc idx )])
                       args
                       (range pcnt))]
      (LLVMSetFunctionCallConv fnc LLVMCCallConv)
      (binding [*fn* fnc
                *locals* newargs
                *block* (LLVMAppendBasicBlock fnc (genname "fblk_"))]
        (LLVMPositionBuilderAtEnd *builder* *block*)
        (LLVMBuildRet *builder* (compile body) (genname "return_"))
        fnc))))

(defmethod compile :module
  [{:keys [body name]}]
  (let [error (new-pointer)
        module (LLVMModuleCreateWithName name )]
    (binding [*module* module
              *builder* (LLVMCreateBuilder)]
      (doseq [x body]
        (stub-global x))
      (doseq [x body]
        (println (:op x) "<______")
        (compile x))
      (LLVMVerifyModule module LLVMAbortProcessAction error)
      (LLVMDumpModule module)
      (LLVMWriteBitcodeToFile module "foo.bc")
      (LLVMDisposeMessage (value-at error))
      module)))

(defn temp-file [prefix ext]
  (let [file (java.io.File/createTempFile prefix ext)]
    (.deleteOnExit file)
    (.getCanonicalPath file)))

(defn dump-module-to-temp-file [module]
  (let [file (temp-file "mod_dump" ".bc")]
    (LLVMWriteBitcodeToFile module file)
    file))


(defn write-object-file [module march] 
  (let [file (dump-module-to-temp-file module)
        ofile (temp-file "o_dump" ".o")
        cmds ["llc" "-filetype=obj" "--march" march "-o" ofile file]
        {:keys [out err exit] :as mp} (apply shell/sh cmds)]
    (println cmds)
    (assert (= exit 0) err)
    
    ofile))

(defn interpret-opt [op]
  (cond (vector? op)
        (let [res (apply shell/sh op)]
          (assert (= 0 (:exit res)) (:err res))
          (string/split (string/trim (:out res)) #"[ \n]"))
        :else
        [op]))

(defn link-object-file [module filename march & opts]
  (let [tmp (write-object-file module march)
        opts (mapcat interpret-opt opts)
        cmds (concat ["gcc" tmp]
                                    opts
                                    ["-o" filename "--shared"])
        _ (println cmds)
        res (apply shell/sh cmds)]
    (assert (= 0 (:exit res)) res)
    (:out res)))



;;;;; TargetMachine Code ;;;;


(defnative Pointer LLVMGetFirstTarget)
(defnative Pointer LLVMGetNextTarget)
(defnative String LLVMGetTargetName)
(defnative String LLVMGetTargetDescription)
(defnative Boolean LLVMTargetHasJIT)
(defnative Boolean LLVMTargetHasTargetMachine)
(defnative Boolean LLVMTargetHasAsmBackend)
(defnative String LLVMGetTarget)
(defnative Pointer LLVMCreateTargetMachine)
(defnative Boolean LLVMTargetMachineEmitToFile)
(defnative Pointer LLVMGetTargetMachineData)

(defn target-info [t]
  {:target t
   :name (LLVMGetTargetName t)
   :desc (LLVMGetTargetDescription t)
   :jit? (LLVMTargetHasJIT t)
   :machine? (LLVMTargetHasTargetMachine t)
   :asm? (LLVMTargetHasAsmBackend t)})

(defn target-seq
  ([]
     (let [ft (LLVMGetFirstTarget)]
       (when ft
         (cons (target-info ft)
               (lazy-seq
                (target-seq ft))))))
  ([t]
     (let [nt (LLVMGetNextTarget t)]
       (when nt
         (cons (target-info nt)
               (lazy-seq
                (target-seq nt)))))))

(defn make-target-machine [module]
  (let [target (LLVMGetTarget module)]
    (println "--->" target)
    (LLVMCreateTargetMachine (:target
                              (first (target-seq)))
                             "x86_64-apple-darwin12.2.0"
                             "i686"
                             ""
                             LLVMCodeGenLevelDefault
                             LLVMRelocPIC
                             LLVMCodeModelDefault)))

(defn emit-to-file [module filename]
  (let [target (make-target-machine module)
        err (new-pointer)
        pass (LLVMCreatePassManager)]
    (LLVMAddTargetData (LLVMGetTargetMachineData target) pass)
    (LLVMRunPassManager pass module)
    
    (when (LLVMTargetMachineEmitToFile target module filename LLVMObjectFile err)
      (assert false (.getString (value-at err) 0)))
    (LLVMDisposeMessage (value-at err))
    (LLVMDisposePassManager pass)))